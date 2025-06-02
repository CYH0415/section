package com.zju.main.section.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zju.main.section.common.ApiResult;
import com.zju.main.section.dto.ScheduleRequest;
import com.zju.main.section.service.Schedule;

/**
 * 排课控制器
 */
@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {
    
    @Autowired
    private Schedule scheduleService;

    /**
     * 自动排课
     */
    @PostMapping("/auto")
    public ApiResult<?> autoSchedule() {
        return scheduleService.auto_schedule();
    }
      /**
     * 手动排课
     */
    @PostMapping("/modify")
    public ApiResult<?> modifySchedule(@RequestBody ScheduleRequest request) {
        return scheduleService.modify_schedule(
            request.getSectionId(), 
            request.getClassroomId(), 
            request.getTimeSlotIds()
        );
    }

    @GetMapping("/admin/query")
    public ApiResult<?> adminQuerySections(
        @RequestParam(required = false) String courseTitle,
        @RequestParam(required = false) String teacherName,
        @RequestParam(required = false) String semester,
        @RequestParam(required = false) Integer year,
        @RequestParam(defaultValue = "1") Integer page,
        @RequestParam(defaultValue = "10") Integer pageSize
        ) {
            return scheduleService.admin_query(courseTitle, semester, year, page, pageSize);
        }
}