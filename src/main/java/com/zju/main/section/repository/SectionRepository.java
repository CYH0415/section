package com.zju.main.section.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.zju.main.entity.Section;

import java.util.List;
import java.util.Map;

@Repository
public interface SectionRepository extends JpaRepository<Section, Integer> {
    
    /**
     * 根据教室ID查询课程章节
     * @param classroomId 教室ID
     * @return 课程章节列表
     */
    List<Section> findByClassroomId(Integer classroomId);
    
    /**
     * 根据教师ID查询课程章节
     * @param teacherId 教师ID
     * @return 课程章节列表
     */
    List<Section> findByTeacherId(Integer teacherId);
    
    /**
     * 查找尚未分配教室或时间段的章节
     * @return 课程章节列表
     */
    @Query(value = """
  SELECT * FROM section
   WHERE (time_slot_ids IS NULL OR JSON_LENGTH(time_slot_ids) = 0)
     AND classroom_id IS NULL
""",
            nativeQuery = true)
    List<Section> findUnscheduledSections();
    
    /**
     * 根据教室ID查询课程章节详细信息（包含课程名称、教师姓名、教室位置）
     * @param classroomId 教室ID
     * @return 包含详细信息的Map列表
     */
    @Query(value = """
        SELECT 
            s.sec_id as secId,
            s.course_id as courseId,
            s.semester as semester,
            s.year as year,
            s.classroom_id as classroomId,
            s.time_slot_ids as timeSlotIds,
            s.teacher_id as teacherId,
            c.title as courseTitle,
            pi.name as teacherName,
            cr.campus as classroomCampus,
            cr.building as classroomBuilding,
            cr.room_number as roomNumber,
            CONCAT(cr.campus, '-', cr.building, '-', cr.room_number) as classroomLocation
        FROM section s
        LEFT JOIN course c ON s.course_id = c.course_id
        LEFT JOIN teacher t ON s.teacher_id = t.user_id
        LEFT JOIN user u ON t.user_id = u.user_id
        LEFT JOIN personal_information pi ON u.personal_infor_id = pi.personal_infor_id
        LEFT JOIN classroom cr ON s.classroom_id = cr.classroom_id
        WHERE s.classroom_id = :classroomId
        """, nativeQuery = true)
    List<Map<String, Object>> findDetailsByClassroomId(@Param("classroomId") Integer classroomId);
    
    /**
     * 根据教师ID查询课程章节详细信息（包含课程名称、教师姓名、教室位置）
     * @param teacherId 教师ID
     * @return 包含详细信息的Map列表
     */
    @Query(value = """
        SELECT 
            s.sec_id as secId,
            s.course_id as courseId,
            s.semester as semester,
            s.year as year,
            s.classroom_id as classroomId,
            s.time_slot_ids as timeSlotIds,
            s.teacher_id as teacherId,
            c.title as courseTitle,
            pi.name as teacherName,
            cr.campus as classroomCampus,
            cr.building as classroomBuilding,
            cr.room_number as roomNumber,
            CONCAT(cr.campus, '-', cr.building, '-', cr.room_number) as classroomLocation
        FROM section s
        LEFT JOIN course c ON s.course_id = c.course_id
        LEFT JOIN teacher t ON s.teacher_id = t.user_id
        LEFT JOIN user u ON t.user_id = u.user_id
        LEFT JOIN personal_information pi ON u.personal_infor_id = pi.personal_infor_id
        LEFT JOIN classroom cr ON s.classroom_id = cr.classroom_id
        WHERE s.teacher_id = :teacherId
        """, nativeQuery = true)
    List<Map<String, Object>> findDetailsByTeacherId(@Param("teacherId") Integer teacherId);
    
    /**
     * 查询所有课程章节的详细信息（包含课程名称、教师姓名、教室位置）
     * @return 包含详细信息的Map列表
     */
    @Query(value = """
        SELECT 
            s.sec_id as secId,
            s.course_id as courseId,
            s.semester as semester,
            s.year as year,
            s.classroom_id as classroomId,
            s.time_slot_ids as timeSlotIds,
            s.teacher_id as teacherId,
            c.title as courseTitle,
            pi.name as teacherName,
            cr.campus as classroomCampus,
            cr.building as classroomBuilding,
            cr.room_number as roomNumber,
            CONCAT(cr.campus, '-', cr.building, '-', cr.room_number) as classroomLocation
        FROM section s
        LEFT JOIN course c ON s.course_id = c.course_id
        LEFT JOIN teacher t ON s.teacher_id = t.user_id
        LEFT JOIN user u ON t.user_id = u.user_id
        LEFT JOIN personal_information pi ON u.personal_infor_id = pi.personal_infor_id
        LEFT JOIN classroom cr ON s.classroom_id = cr.classroom_id
        """, nativeQuery = true)
    List<Map<String, Object>> findAllWithDetails();
    
    /**
     * 根据章节ID查询课程章节详细信息（包含课程名称、教师姓名、教室位置）
     * @param secId 章节ID
     * @return 包含详细信息的Map，如果不存在则返回null
     */
    @Query(value = """
        SELECT 
            s.sec_id as secId,
            s.course_id as courseId,
            s.semester as semester,
            s.year as year,
            s.classroom_id as classroomId,
            s.time_slot_ids as timeSlotIds,
            s.teacher_id as teacherId,
            c.title as courseTitle,
            pi.name as teacherName,
            cr.campus as classroomCampus,
            cr.building as classroomBuilding,
            cr.room_number as roomNumber,
            CONCAT(cr.campus, '-', cr.building, '-', cr.room_number) as classroomLocation
        FROM section s
        LEFT JOIN course c ON s.course_id = c.course_id
        LEFT JOIN teacher t ON s.teacher_id = t.user_id
        LEFT JOIN user u ON t.user_id = u.user_id
        LEFT JOIN personal_information pi ON u.personal_infor_id = pi.personal_infor_id
        LEFT JOIN classroom cr ON s.classroom_id = cr.classroom_id
        WHERE s.sec_id = :secId
        """, nativeQuery = true)
    Map<String, Object> findDetailsBySecId(@Param("secId") Integer secId);
}
