package com.zju.main.section.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zju.main.common.ApiResult;
import com.zju.main.section.repository.SectionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 搜索服务类
 */
@Service
public class Search {

    @Autowired
    private SectionRepository sectionRepository;    /**
     * 搜索课程章节（返回详细信息）
     * 根据提供的老师ID或教室ID搜索关联的section，包含课程名称、教师姓名和教室位置
     *
     * @param teacherId 老师ID（可选）
     * @param classroomId 教室ID（可选）
     * @param year 学年（可选）
     * @param semester 学期（可选）
     * @return 搜索结果（包含详细信息）
     */
    public ApiResult<?> search_section(Integer teacherId, Integer classroomId, Integer year, String semester) {
        List<Map<String, Object>> sectionDetails = new ArrayList<>();

        if (teacherId != null && classroomId != null) {
            // 教师ID 和 教室ID 同时指定：需要特殊处理
            List<Map<String, Object>> teacherSections = sectionRepository.findDetailsByTeacherId(teacherId);
            List<Map<String, Object>> classroomSections = sectionRepository.findDetailsByClassroomId(classroomId);
            
            // 求交集：找到既属于该教师又在该教室的section
            for (Map<String, Object> teacherSection : teacherSections) {
                for (Map<String, Object> classroomSection : classroomSections) {
                    if (teacherSection.get("secId").equals(classroomSection.get("secId"))) {
                        sectionDetails.add(teacherSection);
                        break;
                    }
                }
            }
        } else if (teacherId != null) {
            sectionDetails = sectionRepository.findDetailsByTeacherId(teacherId);
        } else if (classroomId != null) {
            sectionDetails = sectionRepository.findDetailsByClassroomId(classroomId);
        } else {
            // 用户未输入教师或教室ID，则不允许查询，返回错误
            return ApiResult.error("教师ID与教室ID不能同时为空");
        }

        // 添加学期和学年过滤
        if (year != null || (semester != null && !semester.isEmpty())) {
            sectionDetails = sectionDetails.stream()
                .filter(section -> {
                    boolean yearMatch = (year == null) || section.get("year").equals(year);
                    boolean semesterMatch = (semester == null || semester.isEmpty()) || 
                                          semester.equalsIgnoreCase((String) section.get("semester"));
                    return yearMatch && semesterMatch;
                })
                .collect(java.util.stream.Collectors.toList());
        }

        return ApiResult.success("搜索成功", sectionDetails);
    }
    
    /**
     * 根据章节ID查询课程章节详细信息
     * 
     * @param secId 章节ID
     * @return 查询结果（包含详细信息）
     */
    public ApiResult<?> getSectionDetail(Integer secId) {
        // 检查参数
        if (secId == null) {
            return ApiResult.error("章节ID不能为空");
        }
        
        try {
            // 查询章节详细信息
            Map<String, Object> sectionDetail = sectionRepository.findDetailsBySecId(secId);
            
            if (sectionDetail == null) {
                return ApiResult.error("未找到ID为 " + secId + " 的课程章节");
            }
            
            return ApiResult.success("查询成功", sectionDetail);
        } catch (Exception e) {
            return ApiResult.error("查询失败: " + e.getMessage());
        }
    }
}