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
    public ApiResult<?> search_section(Integer teacherId, Integer classroomId) {
        List<Section> sections = new ArrayList<>();
        
        if (teacherId != null && classroomId != null) {
            // 同时指定了教师ID和教室ID
            List<Section> teacherSections = sectionRepository.findByTeacherId(teacherId);
            List<Section> classroomSections = sectionRepository.findByClassroomId(classroomId);
            
            // 取交集
            teacherSections.retainAll(classroomSections);
            sections = teacherSections;
        } else if (teacherId != null) {
            // 只指定了教师ID
            sections = sectionRepository.findByTeacherId(teacherId);
        } else if (classroomId != null) {
            // 只指定了教室ID
            sections = sectionRepository.findByClassroomId(classroomId);
        } else {
            // 都没有指定，返回错误
            return ApiResult.error("请至少指定教师ID或教室ID中的一个");
        }
        
        if (sections.isEmpty()) {
            return ApiResult.success("未找到相关课程章节", sections);
        }
        
        return ApiResult.success("搜索成功", sections);
    }
}
