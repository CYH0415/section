package com.zju.main.section.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zju.main.section.entity.Application;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Integer> {
    
    /**
     * 根据课程章节ID查询申请
     * @param secId 课程章节ID
     * @return 申请
     */
    Application findBySecId(Integer secId);
    
    /**
     * 根据教师ID查询历史申请（分页）
     * @param teacherId 教师ID
     * @param pageable 分页参数
     * @return 申请列表
     */
    Page<Application> findByTeacherIdOrderBySecIdDesc(Integer teacherId, Pageable pageable);
    
    /**
     * 根据教师ID查询历史申请列表
     * @param teacherId 教师ID
     * @return 申请列表
     */
    List<Application> findByTeacherIdOrderBySecIdDesc(Integer teacherId);
    
    /**
     * 联表查询教师的历史申请，包含课程名和上课地点
     */
    @org.springframework.data.jpa.repository.Query(value = """
        SELECT 
            a.*, 
            c.title as courseTitle,
            CONCAT(cr.campus, '-', cr.building, '-', cr.room_number) as classroomLocation
        FROM application a
        LEFT JOIN section s ON a.sec_id = s.sec_id
        LEFT JOIN course c ON s.course_id = c.course_id
        LEFT JOIN classroom cr ON s.classroom_id = cr.classroom_id
        WHERE a.teacher_id = :teacherId
        ORDER BY a.sec_id DESC
        """, nativeQuery = true)
    List<java.util.Map<String, Object>> findHistoryWithDetailByTeacherId(@org.springframework.data.repository.query.Param("teacherId") Integer teacherId);
    
    // @org.springframework.data.jpa.repository.Query(value = """
    //     SELECT 
    //         a.*, 
    //         c.title as courseTitle,
    //         CONCAT(cr.campus, '-', cr.building, '-', cr.room_number) as classroomLocation
    //     FROM application a
    //     LEFT JOIN section s ON a.sec_id = s.sec_id
    //     LEFT JOIN course c ON s.course_id = c.course_id
    //     LEFT JOIN classroom cr ON s.classroom_id = cr.classroom_id
    //     WHERE a.teacher_id = ?1
    //     ORDER BY a.sec_id DESC
    //     LIMIT ?2 OFFSET ?3
    //     """, nativeQuery = true)
    // List<java.util.Map<String, Object>> findHistoryWithDetailByTeacherIdPaged(Integer teacherId, int limit, int offset);
}