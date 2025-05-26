package com.zju.section.service;

import com.zju.section.entity.Course;
import com.zju.section.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zju.section.common.ApiResult;
import com.zju.section.entity.Section;
import com.zju.section.entity.Classroom;
import com.zju.section.entity.TimeSlot;
import com.zju.section.repository.SectionRepository;
import com.zju.section.repository.ClassroomRepository;
import com.zju.section.repository.TimeSlotRepository;

import java.util.*;
import java.util.stream.Collectors;
import java.time.Duration;
import org.springframework.data.util.Pair;

/**
 * 排课服务类
 */
@Service
public class Schedule {
    
    @Autowired
    private SectionRepository sectionRepository;
    
    @Autowired
    private ClassroomRepository classroomRepository;
    
    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Autowired
    private CourseRepository courseRepository;


    /**
     * 自动排课方法
     * 对已经分配了老师、课程和学年的section进行分配教室和时间段
     *
     * @return 排课结果
     */
    @Transactional
    public ApiResult<?> auto_schedule() {
        // 1) 拉元数据
        List<Section> sections = sectionRepository.findUnscheduledSections();
        if (sections.isEmpty()) return ApiResult.success("没有待排课的节次");
        List<Classroom> rooms = classroomRepository.findAll();
        List<TimeSlot> slots   = timeSlotRepository.findAll();

        Map<Integer, Course> courseMap =
                courseRepository.findAllByCourseIdIn(
                        sections.stream().map(Section::getCourseId).distinct().toList()
                ).stream().collect(Collectors.toMap(Course::getCourseId, c->c));
        // 4) 计算平均学时，用于区分“长课”；这里暂将 Period 当学时
        double avgHours = courseMap.values().stream()
                .mapToInt(Course::getPeriod).average().orElse(0);//Period todo

        // 冲突 & 策略记录
        Map<Integer, Set<Integer>> teacherBooked = new HashMap<>();
        Map<Integer, Set<Integer>> roomBooked    = new HashMap<>();
        Integer[] longCourseDay = new Integer[1]; // 用数组便于回溯时“传值”
        Map<String, String> groupBuildingMap = new HashMap<>();

        // 存放最終分配方案 Section -> (slotId, roomId)
        Map<Section, Pair<List<Integer>/*slotIds*/,Integer/*roomId*/>> assignment = new HashMap<>();

        // 2) 回溯尝试
        boolean ok = backtrack(
                sections, rooms, slots, courseMap, avgHours,
                teacherBooked, roomBooked, longCourseDay, groupBuildingMap,
                assignment
        );

        if (!ok) {
            return ApiResult.failure("无法为所有节次完成排课");
        }

        // 3) 持久化 & 返回
        List<Section> scheduled = new ArrayList<>();
        for (var e : assignment.entrySet()) {
            Section sec = e.getKey();
            List<Integer> slotIds = e.getValue().getFirst();
            sec.setTimeSlotIds(slotIds);
            sec.setClassroomId(e.getValue().getSecond());
            sectionRepository.save(sec);
            scheduled.add(sec);
        }
        return ApiResult.success("全部节次排课成功", scheduled);
    }

    /**
     * 回溯调度：为所有 Section 分配 (slotId, roomId)，
     * “同年级+院系同楼”为软优先，其它约束同前。
     * /**
     *      * 回溯调度核心方法：
     *      * - 终止条件：全部节次都已分配
     *      * - MRV（最小剩余值）启发式：优先处理可选方案最少的节次
     *      * - 软优先：若同一“年级+院系”已有固定楼，则先尝试该楼的方案
     *      * - 逐个枚举可行 (slot, room)，并回溯
     *      */
    private boolean backtrack(
            List<Section> sections,
            List<Classroom> rooms,
            List<TimeSlot> slots,
            Map<Integer,Course> courseMap,
            double avgHours,
            Map<Integer,Set<Integer>> teacherBooked,
            Map<Integer,Set<Integer>> roomBooked,
            Integer[] longCourseDay,
            Map<String,String> groupBuildingMap,
            Map<Section, Pair<List<Integer>, Integer>> assignment
    ) {
        // 1. 终止条件：全部安排完
        if (assignment.size() == sections.size()) {
            return true;
        }

        // 2. MRV：选出未分配、可选方案最少的 Section
        Section target = null;
        List<Pair<List<Integer>,Integer>> targetOptions = null;
        int minOpts = Integer.MAX_VALUE;

        for (Section sec : sections) {
            if (assignment.containsKey(sec)) continue;
            Course c = courseMap.get(sec.getCourseId());
            List<Pair<List<Integer>,Integer>> opts = feasibleOptions(
                    sec, c, slots, rooms,
                    teacherBooked, roomBooked,
                    longCourseDay[0], groupBuildingMap,
                    avgHours
            );
            if (opts.isEmpty()) {
                // 任何一个节次无解，整体无解
                return false;
            }
            if (opts.size() < minOpts) {
                minOpts = opts.size();
                target = sec;
                targetOptions = opts;
            }
        }

        // 3. 软优先：尽量用固定楼的方案排
        Course targetCourse = courseMap.get(target.getCourseId());
        String groupKey = targetCourse.getGradeYear() + "_" + targetCourse.getDeptName();
        String fixedBuilding = groupBuildingMap.get(groupKey);
        if (fixedBuilding != null) {
            targetOptions.sort((o1, o2) -> {
                // 找到两个选项对应的教室楼
                String b1 = rooms.stream()
                        .filter(r -> r.getClassroomId().equals(o1.getSecond()))
                        .findFirst()
                        .map(Classroom::getBuilding)
                        .orElse("");
                String b2 = rooms.stream()
                        .filter(r -> r.getClassroomId().equals(o2.getSecond()))
                        .findFirst()
                        .map(Classroom::getBuilding)
                        .orElse("");
                // 在固定楼的方案优先
                boolean m1 = fixedBuilding.equals(b1);
                boolean m2 = fixedBuilding.equals(b2);
                // m1=true 排在前面
                return Boolean.compare(!m1, !m2);
            });
        }

        // 4. 枚举该 Section 的所有可行 (slot, room)
        for (Pair<List<Integer>,Integer> opt : targetOptions) {
            List<Integer> slotIds = opt.getFirst();
            int roomId = opt.getSecond();

            // 保存旧状态以便回溯
            Integer oldLongDay = longCourseDay[0];
            boolean hadBuilding = groupBuildingMap.containsKey(groupKey);
            String oldBuilding = groupBuildingMap.get(groupKey);

            /// 应用：占用所有时段
            assignment.put(target, opt);
            for (int sId : slotIds) {
                teacherBooked.computeIfAbsent(target.getTeacherId(), k->new HashSet<>()).add(sId);
                roomBooked.computeIfAbsent(roomId, k->new HashSet<>()).add(sId);
            }
            // 长课同日
            int needed = courseMap.get(target.getCourseId()).getPeriod();
            if (needed > avgHours && oldLongDay == null) {
                longCourseDay[0] = slots.stream()
                        .filter(s->slotIds.contains(s.getTimeSlotId()))
                        .findFirst().get().getDay();
            }
            // “同年级+院系同楼”策略
            if (!hadBuilding) {
                String building = rooms.stream()
                        .filter(r -> r.getClassroomId().equals(roomId))
                        .findFirst()
                        .map(Classroom::getBuilding)
                        .orElse(null);
                if (building != null) {
                    groupBuildingMap.put(groupKey, building);
                }
            }

            // 5. 递归尝试下一个
            if (backtrack(sections, rooms, slots, courseMap, avgHours,
                    teacherBooked, roomBooked,
                    longCourseDay, groupBuildingMap,
                    assignment)) {
                return true;
            }

            // —— 回溯：撤销选择 ——
            assignment.remove(target);
            for (int sId : slotIds) {
                teacherBooked.get(target.getTeacherId()).remove(sId);
                roomBooked.get(roomId).remove(sId);
            }
            longCourseDay[0] = oldLongDay;
            if (!hadBuilding) {
                groupBuildingMap.remove(groupKey);
            } else {
                groupBuildingMap.put(groupKey, oldBuilding);
            }
        }

        // 所有方案均失败
        return false;
    }
    //找一天里的连续空余时间
    private List<TimeSlot> collectSlotsForDuration(TimeSlot start,
                                                   List<TimeSlot> allSlots,
                                                   int neededHrs) {
        List<TimeSlot> result = new ArrayList<>();
        result.add(start);
        // 已累积时长（小时）
        int accumulated = (int) Duration.between(start.getStartTime(), start.getEndTime()).toHours();
        TimeSlot last = start;

        // 找同一天、紧接着上一个结束的时段，直至累积够 neededHrs
        while (accumulated < neededHrs) {
            // 在 allSlots 里找 day 相同且 startTime == last.endTime 的下一个
            TimeSlot finalLast = last;
            Optional<TimeSlot> nextOpt = allSlots.stream()
                    .filter(s -> s.getDay().equals(finalLast.getDay())
                            && s.getStartTime().equals(finalLast.getEndTime()))
                    .findFirst();
            if (nextOpt.isEmpty()) {
                // 没有连续的可用段，拼不够
                return List.of();
            }
            TimeSlot next = nextOpt.get();
            result.add(next);
            accumulated += (int) Duration.between(next.getStartTime(), next.getEndTime()).toHours();
            last = next;
        }

        return result;
    }
    /**
     * 计算某节次在当前策略下所有合法的 (slotId, roomId) 对
     */
    private List<Pair<List<Integer>,Integer>> feasibleOptions(
            Section sec, Course course,
            List<TimeSlot> slots, List<Classroom> rooms,
            Map<Integer,Set<Integer>> teacherBooked,
            Map<Integer,Set<Integer>> roomBooked,
            Integer longCourseDay,
            Map<String,String> groupBuildingMap,
            double avgHours
    ){
        int teacherId = sec.getTeacherId();
        int neededCap = course.getCapacity();
        String neededType = course.getRequiredRoomType();
        int neededHours = course.getPeriod();
        boolean isLong = neededHours > avgHours; // avgHours 略传入

        List<Pair<List<Integer>,Integer>> opts = new ArrayList<>();
//        String groupKey = course.getGradeYear()+"_"+course.getDeptName();
//        String fixedBuilding = groupBuildingMap.get(groupKey);

        for (TimeSlot slot : slots) {
            if (teacherBooked.getOrDefault(teacherId,Set.of()).contains(slot.getTimeSlotId()))
                continue;
            List<TimeSlot> group = collectSlotsForDuration(slot, slots, neededHours);
            if (group.isEmpty()) continue;
            List<Integer> slotIds = group.stream()
                    .map(TimeSlot::getTimeSlotId)
                    .toList();
            if (isLong && longCourseDay!=null && !slot.getDay().equals(longCourseDay))
                continue;
            long dur = Duration.between(slot.getStartTime(),slot.getEndTime()).toHours();
            if (dur < neededHours) continue;

            for (Classroom r: rooms) {
                if (roomBooked.getOrDefault(r.getClassroomId(),Set.of())
                        .contains(slot.getTimeSlotId())) continue;
                if (r.getCapacity() < neededCap) continue;
                if (!r.getType().equalsIgnoreCase(neededType)) continue;
//                if (fixedBuilding!=null && !fixedBuilding.equals(r.getBuilding())) continue;
                opts.add(Pair.of(slotIds, r.getClassroomId()));
            }
        }
        return opts;
    }
//    @Transactional
//    public ApiResult<?> auto_schedule2() {
//        // 1. 拉出待排课节次
//        List<Section> sections = sectionRepository.findUnscheduledSections();
//        if (sections.isEmpty()) {
//            return ApiResult.success("没有待排课的节次");
//        }
//
//        // 2. 一次性批量拉出所有相关 Course，并做映射
//        List<Integer> courseIds = sections.stream()
//                .map(Section::getCourseId)
//                .distinct()
//                .toList();
//        Map<Integer, Course> courseMap = courseRepository
//                .findAllByCourseIdIn(courseIds)
//                .stream()
//                .collect(Collectors.toMap(Course::getCourseId, c -> c));
//
//        // 3. 计算平均学时，用于区分“长课”
//        double avgHours = courseMap.values().stream()
//                .mapToInt(Course::getPeriod)//暂时把学分当学时了，todo
//                .average()
//                .orElse(0);
//
//        // 4. 按学时降序排，长课优先
//        sections.sort(Comparator.comparingInt(
//                sec -> -courseMap.get(sec.getCourseId()).getPeriod()));
//
//        // 5. 拉所有教室、时段
//        List<Classroom> rooms = classroomRepository.findAll();
//        List<TimeSlot> slots = timeSlotRepository.findAll();
//
//        // 冲突检测：教师/教室已占用的时段
//        Map<Integer, Set<Integer>> teacherBooked = new HashMap<>();
//        Map<Integer, Set<Integer>> roomBooked    = new HashMap<>();
//
//        // 记录“长课”分配的同一天
//        Integer longCourseDay = null;
//        // 记录“年级+院系” -> 教学楼todo有可能直接由专业决定教学楼，还有校区需要加入。
//        Map<String, String> groupBuildingMap = new HashMap<>();
//
//        List<Section> scheduled = new ArrayList<>();
//
//        // 6. 开始调度
//        for (Section sec : sections) {
//            int teacherId = sec.getTeacherId();
//            Course course = courseMap.get(sec.getCourseId());
//            int neededCapacity    = course.getCapacity();
//            String neededRoomType = course.getRequiredRoomType();
//            int neededHours       = course.getPeriod();
//            boolean isLongCourse  = neededHours > avgHours;
//
//            boolean assigned = false;
//            String groupKey = course.getGradeYear() + "_" + course.getDeptName();
//            String fixedBuilding = groupBuildingMap.get(groupKey);
//
//            // 遍历所有时段
//            for (TimeSlot slot : slots) {
//                int slotId = slot.getTimeSlotId();
//
//                // （1）教师时段冲突
//                if (teacherBooked.getOrDefault(teacherId, Set.of()).contains(slotId)) {
//                    continue;
//                }
//                // （2）长课同日约束
//                if (isLongCourse && longCourseDay != null && !slot.getDay().equals(longCourseDay)) {
//                    continue;
//                }
//                // （3）学时时长约束
//                long duration = Duration.between(slot.getStartTime(), slot.getEndTime()).toHours();
//                if (duration < neededHours) {
//                    continue;
//                }
//
//                // 遍历教室
//                for (Classroom room : rooms) {
//                    int roomId = room.getClassroomId();
//
//                    // （4）教室时段冲突
//                    if (roomBooked.getOrDefault(roomId, Set.of()).contains(slotId)) {
//                        continue;
//                    }
//                    // （5）容量约束
//                    if (room.getCapacity() < neededCapacity) {
//                        continue;
//                    }
//                    // （6）类型约束
//                    if (!room.getType().equalsIgnoreCase(neededRoomType)) {
//                        continue;
//                    }
//                    // （7）同年级同院系同楼
//                    if (fixedBuilding != null && !fixedBuilding.equals(room.getBuilding())) {
//                        continue;
//                    }
//
//                    // —— 满足所有约束，执行分配 ——
//                    sec.setTimeSlotIds(slotId);
//                    sec.setClassroomId(roomId);
//                    sectionRepository.save(sec);
//
//                    // 更新冲突记录
//                    teacherBooked
//                            .computeIfAbsent(teacherId, k -> new HashSet<>())
//                            .add(slotId);
//                    roomBooked
//                            .computeIfAbsent(roomId, k -> new HashSet<>())
//                            .add(slotId);
//
//                    // 记录“长课同日”以及“年级+院系同楼”策略
//                    if (isLongCourse && longCourseDay == null) {
//                        longCourseDay = slot.getDay();
//                    }
//                    groupBuildingMap.putIfAbsent(groupKey, room.getBuilding());
//
//                    scheduled.add(sec);
//                    assigned = true;
//                    break;
//                }
//                if (assigned) break;
//            }
//
//            if (!assigned) {
//                return ApiResult.failure("节次 " + sec.getSecId() + " 无合适教室/时段");
//            }
//        }
//
//        return ApiResult.success(scheduled);
//    }




    /**
     * 手动排课方法
     * 对已经分配了老师、课程和学时的section进行手动分配教室和时间段
     * 
     * @param sectionId 课程章节ID
     * @param classroomId 教室ID
     * @param timeSlotIds 时间段ID
     * @return 排课结果
     */
    @Transactional
    public ApiResult<?> modify_schedule(Integer sectionId, Integer classroomId, List<Integer> timeSlotIds) {
        // 检查section是否存在
        Optional<Section> optionalSection = sectionRepository.findById(sectionId);
        if (!optionalSection.isPresent()) {
            return ApiResult.error("课程章节不存在，排课失败");
        }
        
        // 检查classroom是否存在
        Optional<Classroom> optionalClassroom = classroomRepository.findById(classroomId);
        if (!optionalClassroom.isPresent()) {
            return ApiResult.error("教室不存在，排课失败");
        }

        List<TimeSlot> found = timeSlotRepository.findAllById(timeSlotIds);
        if (found.size() != timeSlotIds.size()) {
            return ApiResult.error("部分时间段不存在，排课失败");
        }
        
        // 更新section的教室和时间段
        Section section = optionalSection.get();
        section.setClassroomId(classroomId);
        section.setTimeSlotIds(timeSlotIds);
        
        // 保存更新后的section
        sectionRepository.save(section);
        
        return ApiResult.success("手动排课成功");
    }
}
