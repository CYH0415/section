package com.zju.section.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zju.section.common.ApiResult;
import com.zju.section.entity.Section;
import com.zju.section.repository.SectionRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索服务类
 */
@Service
public class Search {

    @Autowired
    private SectionRepository sectionRepository;

    /**
     * 搜索课程章节
     * 根据提供的老师ID或教室ID搜索关联的section
     *
     * @param teacherId 老师ID（可选）
     * @param classroomId 教室ID（可选）
     * @return 搜索结果
     */
public ApiResult<?> search_section(Integer teacherId, Integer classroomId, Integer year, String semester) {
    List<Section> sections = new ArrayList<>();

    if (teacherId != null && classroomId != null) {
        // 教师ID 和 教室ID 同时指定：求交集
        List<Section> teacherSections = sectionRepository.findByTeacherId(teacherId);
        List<Section> classroomSections = sectionRepository.findByClassroomId(classroomId);
        teacherSections.retainAll(classroomSections);
        sections = teacherSections;
    } else if (teacherId != null) {
        sections = sectionRepository.findByTeacherId(teacherId);
    } else if (classroomId != null) {
        sections = sectionRepository.findByClassroomId(classroomId);
    } else {
        // 用户未输入教师或教室ID，则不允许查询，返回错误
        return ApiResult.error("教师ID与教室ID不能同时为空");
    }

    // 添加学期和学年过滤
    sections.removeIf(s ->
        s.getYear().getValue() != year || !s.getSemester().equalsIgnoreCase(semester)
    );

    return ApiResult.success("搜索成功", sections);
}
}