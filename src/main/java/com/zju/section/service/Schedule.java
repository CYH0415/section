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
        List<Section> nowsections=sectionRepository.findAll();
        if (sections.isEmpty()) return ApiResult.success("没有待排课的节次",nowsections);
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
//        Integer[] longCourseDay = new Integer[1]; // 用数组便于回溯时“传值”
        Map<String, String> groupBuildingMap = new HashMap<>();

        // 存放最終分配方案 Section -> (slotId, roomId)
        Map<Section, Pair<List<Integer>/*slotIds*/,Integer/*roomId*/>> assignment = new HashMap<>();

        // 2) 回溯尝试
        boolean ok = backtrack(
                sections, rooms, slots, courseMap, avgHours,
                teacherBooked, roomBooked, groupBuildingMap,
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
//            Integer[] longCourseDay,
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
                     groupBuildingMap,
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
//            Integer oldLongDay = longCourseDay[0];
            boolean hadBuilding = groupBuildingMap.containsKey(groupKey);
            String oldBuilding = groupBuildingMap.get(groupKey);

            /// 应用：占用所有时段
            assignment.put(target, opt);
            for (int sId : slotIds) {
                teacherBooked.computeIfAbsent(target.getTeacherId(), k->new HashSet<>()).add(sId);
                roomBooked.computeIfAbsent(roomId, k->new HashSet<>()).add(sId);
            }
//            // 长课同日
//            int needed = courseMap.get(target.getCourseId()).getPeriod();
//            if (needed > avgHours && oldLongDay == null) {
//                longCourseDay[0] = slots.stream()
//                        .filter(s->slotIds.contains(s.getTimeSlotId()))
//                        .findFirst().get().getDay();
//            }
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
                     groupBuildingMap,
                    assignment)) {
                return true;
            }

            // —— 回溯：撤销选择 ——
            assignment.remove(target);
            for (int sId : slotIds) {
                teacherBooked.get(target.getTeacherId()).remove(sId);
                roomBooked.get(roomId).remove(sId);
            }
//            longCourseDay[0] = oldLongDay;
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
        // 1. 先从 allSlots 里取出同一天的，并按 startTime 排序
        List<TimeSlot> daySlots = allSlots.stream()
                .filter(s -> s.getDay().equals(start.getDay()))
                .sorted(Comparator.comparing(TimeSlot::getStartTime))
                .toList();

        // 2. 找到起始时段在这一天列表里的下标
        int idx = -1;
        for (int i = 0; i < daySlots.size(); i++) {
            if (daySlots.get(i).getTimeSlotId().equals(start.getTimeSlotId())) {
                idx = i;
                break;
            }
        }
        if (idx < 0) {
            // 找不到起始时段
            return List.of();
        }

        // 3. 准备结果，先放入 start
        List<TimeSlot> result = new ArrayList<>(neededHrs);
        result.add(start);

        // 4. 依次往后取 neededHrs-1 段，校验间隔
        for (int k = 1; k < neededHrs; k++) {
            int nextIdx = idx + k;
            if (nextIdx >= daySlots.size()) {
                // 已经超出当天可用段数
                return List.of();
            }
//            TimeSlot prev = result.get(k - 1);
            TimeSlot next = daySlots.get(nextIdx);
//
//            // 计算 prev.endTime 到 next.startTime 的分钟差
//            long gapMinutes = Duration.between(prev.getEndTime(), next.getStartTime()).toMinutes();
//            if (gapMinutes != 10) {
//                // 如果不是正好 10 分钟，就断开了
//                return List.of();
//            }

            result.add(next);
        }

        return result;
    }
    /**
     * 计算某节次在当前策略下所有合法的 (slotId, roomId) 对
     */
    private List<Pair<List<Integer>,Integer>> feasibleOptions(
            Section sec, Course course,
            List<TimeSlot> slots, List<Classroom> roomss,
            Map<Integer,Set<Integer>> teacherBooked,
            Map<Integer,Set<Integer>> roomBooked,
//            Integer longCourseDay,
            Map<String,String> groupBuildingMap,
            double avgHours
    ){
        int teacherId = sec.getTeacherId();
        int neededCap = course.getCapacity();
        String neededType = course.getRequiredRoomType();
        int neededHours = course.getPeriod();
//        boolean isLong = neededHours > avgHours; // avgHours 略传入

        List<Pair<List<Integer>,Integer>> opts = new ArrayList<>();
//        String groupKey = course.getGradeYear()+"_"+course.getDeptName();
//        String fixedBuilding = groupBuildingMap.get(groupKey);
        List<Classroom> rooms = classroomRepository.findAllByDeptName(course.getDeptName());
        for (TimeSlot slot : slots) {
            if (teacherBooked.getOrDefault(teacherId,Set.of()).contains(slot.getTimeSlotId()))
                continue;
            List<TimeSlot> group = collectSlotsForDuration(slot, slots, neededHours);
            if (group.isEmpty()) continue;
            List<Integer> slotIds = group.stream()
                    .map(TimeSlot::getTimeSlotId)
                    .toList();
//            if (isLong && longCourseDay!=null && !slot.getDay().equals(longCourseDay))
//                continue;
//            long dur = Duration.between(slot.getStartTime(),slot.getEndTime()).toHours();
//            if (dur < neededHours) continue;

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
    @Transactional(readOnly = true)
public ApiResult<?> admin_query(String courseTitle, String semester, Integer year, int page, int pageSize) {
    List<Section> allSections = sectionRepository.findAll();

    // 筛选逻辑
    List<Section> filtered = allSections.stream().filter(sec -> {
        boolean match = true;
        if (semester != null && !semester.isEmpty())
            match &= sec.getSemester().equalsIgnoreCase(semester);
        if (year != null)
            match &= sec.getYear().getValue() == year;
        if (courseTitle != null && !courseTitle.isEmpty()) {
            Course course = courseRepository.findById(sec.getCourseId()).orElse(null);
            match &= (course != null && course.getTitle().contains(courseTitle));
        }
        return match;
    }).toList();

    // 分页处理
    int total = filtered.size();
    int from = Math.min((page - 1) * pageSize, total);
    int to = Math.min(from + pageSize, total);

    List<Map<String, Object>> records = new ArrayList<>();
    for (Section sec : filtered.subList(from, to)) {
        Map<String, Object> map = new HashMap<>();
        map.put("secId", sec.getSecId());
        map.put("courseId", sec.getCourseId());
        map.put("teacherId", sec.getTeacherId());
        map.put("classroomId", sec.getClassroomId());
        map.put("semester", sec.getSemester());
        map.put("year", sec.getYear().getValue());
        map.put("timeSlotIds", sec.getTimeSlotIds());

        courseRepository.findById(sec.getCourseId()).ifPresent(c -> map.put("courseTitle", c.getTitle()));

        records.add(map);
    }

    Map<String, Object> result = new HashMap<>();
    result.put("total", total);
    result.put("records", records);
    return ApiResult.success(result);
}
}