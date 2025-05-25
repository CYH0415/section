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

        double avgHours = courseMap.values().stream()
                .mapToInt(Course::getCredits).average().orElse(0);//credits todo

        // 冲突 & 策略记录
        Map<Integer, Set<Integer>> teacherBooked = new HashMap<>();
        Map<Integer, Set<Integer>> roomBooked    = new HashMap<>();
        Integer[] longCourseDay = new Integer[1]; // 用数组便于回溯时“传值”
        Map<String, String> groupBuildingMap = new HashMap<>();

        // 存放最終分配方案
        Map<Section, Pair<Integer/*slotId*/,Integer/*roomId*/>> assignment = new HashMap<>();

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
            sec.setTimeSlotId(e.getValue().getFirst());
            sec.setClassroomId(e.getValue().getSecond());
            sectionRepository.save(sec);
            scheduled.add(sec);
        }
        return ApiResult.success("全部节次排课成功", scheduled);
    }

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
            Map<Section,Pair<Integer,Integer>> assignment
    ){
        // 递归终止：全部安排完
        if (assignment.size() == sections.size()) return true;

        // MRV：找出未分配、可用组合最少的那个节次
        Section target = null;
        List<Pair<Integer,Integer>> targetOptions = null;
        int minOpts = Integer.MAX_VALUE;

        for (Section sec : sections) if (!assignment.containsKey(sec)) {
            Course c = courseMap.get(sec.getCourseId());
            List<Pair<Integer,Integer>> opts = feasibleOptions(
                    sec, c, slots, rooms,
                    teacherBooked, roomBooked,
                    longCourseDay[0], groupBuildingMap,
                    avgHours
            );
            if (opts.isEmpty()) return false;  // 某节次无解，剪枝
            if (opts.size() < minOpts) {
                minOpts = opts.size();
                target = sec;
                targetOptions = opts;
            }
        }

        // 枚举该节次的所有可行 (slot, room)
        for (var opt : targetOptions) {
            int slotId = opt.getFirst(), roomId = opt.getSecond();
            // 保存旧策略，便于回溯
            Integer oldLongDay = longCourseDay[0];
            String groupKey = target.getYear() + "_" + courseMap.get(target.getCourseId()).getDeptName();
            boolean builtHad = groupBuildingMap.containsKey(groupKey);
            String oldBuilding = groupBuildingMap.get(groupKey);

            // 应用这次选择
            assignment.put(target, opt);
            teacherBooked.computeIfAbsent(target.getTeacherId(), k->new HashSet<>()).add(slotId);
            roomBooked.computeIfAbsent(roomId, k->new HashSet<>()).add(slotId);
            // 记录长课同日
            int neededHours = courseMap.get(target.getCourseId()).getCredits();
            if (neededHours > avgHours && longCourseDay[0] == null) {
                // 用 slots 列表中查到的 day
                slots.stream().filter(s->s.getTimeSlotId()==slotId)
                        .findFirst().ifPresent(s-> longCourseDay[0]=s.getDay());
            }
            // 记录同年级同院系同楼
            if (!builtHad) {
                groupBuildingMap.put(groupKey,
                        rooms.stream().filter(r->r.getClassroomId()==roomId)
                                .findFirst().get().getBuilding()
                );
            }

            // 递归
            if (backtrack(sections, rooms, slots, courseMap, avgHours,
                    teacherBooked, roomBooked, longCourseDay,
                    groupBuildingMap, assignment)) {
                return true;
            }

            // 回溯：撤销
            assignment.remove(target);
            teacherBooked.get(target.getTeacherId()).remove(slotId);
            roomBooked.get(roomId).remove(slotId);
            longCourseDay[0] = oldLongDay;
            if (!builtHad) groupBuildingMap.remove(groupKey);
            else groupBuildingMap.put(groupKey, oldBuilding);
        }

        // 全部选项都失败
        return false;
    }

    /**
     * 计算某节次在当前策略下所有合法的 (slotId, roomId) 对
     */
    private List<Pair<Integer,Integer>> feasibleOptions(
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
        int neededHours = course.getCredits();
        boolean isLong = neededHours > avgHours; // avgHours 略传入

        List<Pair<Integer,Integer>> opts = new ArrayList<>();
        String groupKey = sec.getYear()+"_"+course.getDeptName();
        String fixedBuilding = groupBuildingMap.get(groupKey);

        for (TimeSlot slot : slots) {
            if (teacherBooked.getOrDefault(teacherId,Set.of()).contains(slot.getTimeSlotId()))
                continue;
            if (isLong && longCourseDay!=null && !slot.getDay().equals(longCourseDay))
                continue;
            long dur = Duration.between(slot.getStartTime(),slot.getEndTime()).toHours();
            if (dur < neededHours) continue;

            for (Classroom r: rooms) {
                if (roomBooked.getOrDefault(r.getClassroomId(),Set.of())
                        .contains(slot.getTimeSlotId())) continue;
                if (r.getCapacity() < neededCap) continue;
                if (!r.getType().equalsIgnoreCase(neededType)) continue;
                if (fixedBuilding!=null && !fixedBuilding.equals(r.getBuilding())) continue;
                opts.add(Pair.of(slot.getTimeSlotId(), r.getClassroomId()));
            }
        }
        return opts;
    }
    @Transactional
    public ApiResult<?> auto_schedule2() {
        // 1. 拉出待排课节次
        List<Section> sections = sectionRepository.findUnscheduledSections();
        if (sections.isEmpty()) {
            return ApiResult.success("没有待排课的节次");
        }

        // 2. 一次性批量拉出所有相关 Course，并做映射
        List<Integer> courseIds = sections.stream()
                .map(Section::getCourseId)
                .distinct()
                .toList();
        Map<Integer, Course> courseMap = courseRepository
                .findAllByCourseIdIn(courseIds)
                .stream()
                .collect(Collectors.toMap(Course::getCourseId, c -> c));

        // 3. 计算平均学时，用于区分“长课”
        double avgHours = courseMap.values().stream()
                .mapToInt(Course::getCredits)//暂时把学分当学时了，todo
                .average()
                .orElse(0);

        // 4. 按学时降序排，长课优先
        sections.sort(Comparator.comparingInt(
                sec -> -courseMap.get(sec.getCourseId()).getCredits()));

        // 5. 拉所有教室、时段
        List<Classroom> rooms = classroomRepository.findAll();
        List<TimeSlot> slots = timeSlotRepository.findAll();

        // 冲突检测：教师/教室已占用的时段
        Map<Integer, Set<Integer>> teacherBooked = new HashMap<>();
        Map<Integer, Set<Integer>> roomBooked    = new HashMap<>();

        // 记录“长课”分配的同一天
        Integer longCourseDay = null;
        // 记录“年级+院系” -> 教学楼todo有可能直接由专业决定教学楼，还有校区需要加入。
        Map<String, String> groupBuildingMap = new HashMap<>();

        List<Section> scheduled = new ArrayList<>();

        // 6. 开始调度
        for (Section sec : sections) {
            int teacherId = sec.getTeacherId();
            Course course = courseMap.get(sec.getCourseId());
            int neededCapacity    = course.getCapacity();
            String neededRoomType = course.getRequiredRoomType();
            int neededHours       = course.getCredits();
            boolean isLongCourse  = neededHours > avgHours;

            boolean assigned = false;
            String groupKey = sec.getYear() + "_" + course.getDeptName();
            String fixedBuilding = groupBuildingMap.get(groupKey);

            // 遍历所有时段
            for (TimeSlot slot : slots) {
                int slotId = slot.getTimeSlotId();

                // （1）教师时段冲突
                if (teacherBooked.getOrDefault(teacherId, Set.of()).contains(slotId)) {
                    continue;
                }
                // （2）长课同日约束
                if (isLongCourse && longCourseDay != null && !slot.getDay().equals(longCourseDay)) {
                    continue;
                }
                // （3）学时时长约束
                long duration = Duration.between(slot.getStartTime(), slot.getEndTime()).toHours();
                if (duration < neededHours) {
                    continue;
                }

                // 遍历教室
                for (Classroom room : rooms) {
                    int roomId = room.getClassroomId();

                    // （4）教室时段冲突
                    if (roomBooked.getOrDefault(roomId, Set.of()).contains(slotId)) {
                        continue;
                    }
                    // （5）容量约束
                    if (room.getCapacity() < neededCapacity) {
                        continue;
                    }
                    // （6）类型约束
                    if (!room.getType().equalsIgnoreCase(neededRoomType)) {
                        continue;
                    }
                    // （7）同年级同院系同楼
                    if (fixedBuilding != null && !fixedBuilding.equals(room.getBuilding())) {
                        continue;
                    }

                    // —— 满足所有约束，执行分配 ——
                    sec.setTimeSlotId(slotId);
                    sec.setClassroomId(roomId);
                    sectionRepository.save(sec);

                    // 更新冲突记录
                    teacherBooked
                            .computeIfAbsent(teacherId, k -> new HashSet<>())
                            .add(slotId);
                    roomBooked
                            .computeIfAbsent(roomId, k -> new HashSet<>())
                            .add(slotId);

                    // 记录“长课同日”以及“年级+院系同楼”策略
                    if (isLongCourse && longCourseDay == null) {
                        longCourseDay = slot.getDay();
                    }
                    groupBuildingMap.putIfAbsent(groupKey, room.getBuilding());

                    scheduled.add(sec);
                    assigned = true;
                    break;
                }
                if (assigned) break;
            }

            if (!assigned) {
                return ApiResult.failure("节次 " + sec.getSecId() + " 无合适教室/时段");
            }
        }

        return ApiResult.success(scheduled);
    }




    /**
     * 手动排课方法
     * 对已经分配了老师、课程和学时的section进行手动分配教室和时间段
     * 
     * @param sectionId 课程章节ID
     * @param classroomId 教室ID
     * @param timeSlotId 时间段ID
     * @return 排课结果
     */
    @Transactional
    public ApiResult<?> modify_schedule(Integer sectionId, Integer classroomId, Integer timeSlotId) {
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
        
        // 检查timeSlot是否存在
        Optional<TimeSlot> optionalTimeSlot = timeSlotRepository.findById(timeSlotId);
        if (!optionalTimeSlot.isPresent()) {
            return ApiResult.error("时间段不存在，排课失败");
        }
        
        // 更新section的教室和时间段
        Section section = optionalSection.get();
        section.setClassroomId(classroomId);
        section.setTimeSlotId(timeSlotId);
        
        // 保存更新后的section
        sectionRepository.save(section);
        
        return ApiResult.success("手动排课成功");
    }
}
