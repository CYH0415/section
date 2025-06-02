package com.zju.section.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.zju.section.entity.Section;

import java.util.List;

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
}