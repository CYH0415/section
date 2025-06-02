package com.zju.main.section.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zju.main.common.ApiResult;
import com.zju.main.section.dto.ApplicationHistoryDetailResponse;
import com.zju.main.section.dto.QueryApplicationResponse;
import com.zju.main.section.dto.TeacherApplicationHistoryResponse;
import com.zju.main.entity.Application;
import com.zju.main.entity.Section;
import com.zju.main.section.repository.ApplicationRepository;
import com.zju.main.section.repository.SectionRepository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * 申请管理服务类
 */
@Service
public class ApplicationManager {
    
    @Autowired
    private ApplicationRepository applicationRepository;
    
    @Autowired
    private SectionRepository sectionRepository;
      /**
     * 添加申请
     * 
     * @param secId 课程章节ID
     * @param reason 申请原因
     * @param teacherId 教师ID
     * @return 添加结果
     */
    @Transactional
    public ApiResult<?> add_application(Integer secId, String reason, Integer teacherId) {
        // 检查参数
        if (secId == null) {
            return ApiResult.error("课程章节ID不能为空");
        }
        
        if (reason == null || reason.trim().isEmpty()) {
            return ApiResult.error("申请原因不能为空");
        }
        
        if (teacherId == null) {
            return ApiResult.error("教师ID不能为空");
        }
        
        // 检查课程章节是否存在
        Optional<Section> optionalSection = sectionRepository.findById(secId);
        if (!optionalSection.isPresent()) {
            return ApiResult.error("课程章节不存在");
        }
        
        // 检查申请是否已存在
        Application existingApplication = applicationRepository.findBySecId(secId);
        if (existingApplication != null) {
            return ApiResult.error("该课程章节已有申请记录");
        }
          // 创建新申请
        Application application = new Application();
        application.setSecId(secId);
        application.setReason(reason);
        application.setTeacherId(teacherId);
        application.setFinalDecision(false);
        
        // 保存申请
        application = applicationRepository.save(application);
        
        return ApiResult.success("添加申请成功", application);
    }

    public ApiResult<?> query_application(int page, int size) {
    Pageable pageable = PageRequest.of(page - 1, size);
    Page<Application> applicationPage = applicationRepository.findAll(pageable);

    // 可选：返回自定义分页结构
    return ApiResult.success("查询成功", new QueryApplicationResponse(applicationPage));
    }
    
    /**
     * 处理申请
     * 
     * @param secId 课程章节ID
     * @param suggestion 处理建议
     * @param finalDecision 最终决定
     * @return 处理结果
     */
    @Transactional
    public ApiResult<?> process_application(Integer secId, String suggestion, Boolean finalDecision) {
        // 检查参数
        if (secId == null) {
            return ApiResult.error("课程章节ID不能为空");
        }
        
        if (suggestion == null || suggestion.trim().isEmpty()) {
            return ApiResult.error("处理建议不能为空");
        }
        
        if (finalDecision == null) {
            return ApiResult.error("最终决定不能为空");
        }
        
        // 检查申请是否存在
        Application application = applicationRepository.findBySecId(secId);
        if (application == null) {
            return ApiResult.error("申请记录不存在");
        }
        
        // 更新申请
        application.setSuggestion(suggestion);
        application.setFinalDecision(finalDecision);
        
        // 保存更新后的申请
        application = applicationRepository.save(application);
        
        return ApiResult.success("处理申请成功", application);
    }
    
    /**
     * 根据教师ID查询历史申请（分页）
     * 
     * @param teacherId 教师ID
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 查询结果
     */    public ApiResult<?> queryApplicationsByTeacher(Integer teacherId, int page, int size) {
        if (teacherId == null) {
            return ApiResult.error("教师ID不能为空");
        }
        if (page < 1) page = 1;
        if (size < 1) size = 10;
        try {
            List<java.util.Map<String, Object>> all = applicationRepository.findHistoryWithDetailByTeacherId(teacherId);
            int from = (page - 1) * size;
            int to = Math.min(from + size, all.size());
            if (from >= all.size()) {
                return ApiResult.success("查询教师申请历史成功", java.util.Collections.emptyList());
            }
            List<ApplicationHistoryDetailResponse> result = new java.util.ArrayList<>();
            for (java.util.Map<String, Object> row : all.subList(from, to)) {
                ApplicationHistoryDetailResponse dto = new ApplicationHistoryDetailResponse();
                dto.setSecId((Integer) row.get("sec_id"));
                dto.setReason((String) row.get("reason"));
                dto.setTeacherId((Integer) row.get("teacher_id"));
                dto.setSuggestion((String) row.get("suggestion"));
                dto.setFinalDecision(row.get("final") != null ? ((Boolean) row.get("final")) : null);
                dto.setCourseTitle((String) row.get("courseTitle"));
                dto.setClassroomLocation((String) row.get("classroomLocation"));
                result.add(dto);
            }
            java.util.Map<String, Object> resp = new java.util.HashMap<>();
            resp.put("total", all.size());
            resp.put("items", result);
            return ApiResult.success("查询教师申请历史成功", resp);
        } catch (Exception e) {
            return ApiResult.error("查询教师申请历史失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据教师ID查询所有历史申请
     * 
     * @param teacherId 教师ID
     * @return 查询结果
     */    public ApiResult<?> queryAllApplicationsByTeacher(Integer teacherId) {
        if (teacherId == null) {
            return ApiResult.error("教师ID不能为空");
        }
        try {
            List<java.util.Map<String, Object>> all = applicationRepository.findHistoryWithDetailByTeacherId(teacherId);
            java.util.List<com.zju.main.section.dto.ApplicationHistoryDetailResponse> result = new java.util.ArrayList<>();
            for (java.util.Map<String, Object> row : all) {
                com.zju.main.section.dto.ApplicationHistoryDetailResponse dto = new com.zju.main.section.dto.ApplicationHistoryDetailResponse();
                dto.setSecId((Integer) row.get("sec_id"));
                dto.setReason((String) row.get("reason"));
                dto.setTeacherId((Integer) row.get("teacher_id"));
                dto.setSuggestion((String) row.get("suggestion"));
                dto.setFinalDecision(row.get("final") != null ? ((Boolean) row.get("final")) : null);
                dto.setCourseTitle((String) row.get("courseTitle"));
                dto.setClassroomLocation((String) row.get("classroomLocation"));
                result.add(dto);
            }
            java.util.Map<String, Object> resp = new java.util.HashMap<>();
            resp.put("total", result.size());
            resp.put("items", result);
            return ApiResult.success("查询教师申请历史成功", resp);
        } catch (Exception e) {
            return ApiResult.error("查询教师申请历史失败: " + e.getMessage());
        }
    }
}