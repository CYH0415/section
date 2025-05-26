package com.zju.section.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zju.section.common.ApiResult;
import com.zju.section.dto.ScheduleRequest;
import com.zju.section.service.Schedule;

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
}
