package com.zju.main.section.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zju.main.section.common.ApiResult;
import com.zju.main.section.dto.AddApplicationRequest;
import com.zju.main.section.dto.ProcessApplicationRequest;
import com.zju.main.section.service.ApplicationManager;

/**
 * 申请管理控制器
 */
@RestController
@RequestMapping("/api/application")
public class ApplicationController {
    
    @Autowired
    private ApplicationManager applicationManager;
      /**
     * 添加申请
     */    @PostMapping("/add")
    public ApiResult<?> addApplication(@RequestBody AddApplicationRequest request) {
        return applicationManager.add_application(
            request.getSecId(), 
            request.getReason(), 
            request.getTeacherId()
        );
    }

    @GetMapping("/query")
    public ApiResult<?> queryApplications(@RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        return applicationManager.query_application(page, size);
    }

    /**
     * 处理申请
     */
    @PostMapping("/process")
    public ApiResult<?> processApplication(@RequestBody ProcessApplicationRequest request) {
        return applicationManager.process_application(
            request.getSecId(), 
            request.getSuggestion(), 
            request.getFinalDecision()
        );
    }
    
    /**
     * 根据教师ID查询所有历史申请
     */
    @GetMapping("/teacher/{teacherId}/all")
    public ApiResult<?> queryAllTeacherApplications(@PathVariable Integer teacherId) {
        return applicationManager.queryAllApplicationsByTeacher(teacherId);
    }
}