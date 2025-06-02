package com.zju.section.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zju.section.common.ApiResult;
import com.zju.section.dto.AddApplicationRequest;
import com.zju.section.dto.ProcessApplicationRequest;
import com.zju.section.service.ApplicationManager;

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
     */
    @PostMapping("/add")
    public ApiResult<?> addApplication(@RequestBody AddApplicationRequest request) {
        return applicationManager.add_application(
            request.getSecId(), 
            request.getReason(), 
            request.getTeacher()
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
}