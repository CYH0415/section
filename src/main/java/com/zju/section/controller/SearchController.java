package com.zju.section.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zju.section.common.ApiResult;
import com.zju.section.service.Search;

/**
 * 搜索控制器
 */
@RestController
@RequestMapping("/api/search")
public class SearchController {
    
    @Autowired
    private Search searchService;
    
    /**
     * 搜索课程章节
     */
    @GetMapping("/section")
    public ApiResult<?> searchSection(
        @RequestParam(required = false) Integer teacherId,
        @RequestParam(required = false) Integer classroomId,
        @RequestParam Integer year,
        @RequestParam String semester) {
    return searchService.search_section(teacherId, classroomId, year, semester);
}
}