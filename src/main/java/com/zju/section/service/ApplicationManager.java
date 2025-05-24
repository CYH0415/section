package com.zju.section.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zju.section.common.ApiResult;
import com.zju.section.entity.Application;
import com.zju.section.entity.Section;
import com.zju.section.repository.ApplicationRepository;
import com.zju.section.repository.SectionRepository;

import java.util.Optional;

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
     * @param teacher 教师信息
     * @return 添加结果
     */
    @Transactional
    public ApiResult<?> add_application(Integer secId, String reason, String teacher) {
        // 检查参数
        if (secId == null) {
            return ApiResult.error("课程章节ID不能为空");
        }
        
        if (reason == null || reason.trim().isEmpty()) {
            return ApiResult.error("申请原因不能为空");
        }
        
        if (teacher == null || teacher.trim().isEmpty()) {
            return ApiResult.error("教师信息不能为空");
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
        application.setTeacher(teacher);
        application.setFinalDecision(false);
        
        // 保存申请
        application = applicationRepository.save(application);
        
        return ApiResult.success("添加申请成功", application);
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
}
