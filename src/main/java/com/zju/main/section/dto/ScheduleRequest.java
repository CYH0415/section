package com.zju.main.section.dto;

import lombok.Data;

import java.util.List;

/**
 * 排课请求DTO
 */
@Data
public class ScheduleRequest {
    private Integer sectionId;
    private Integer classroomId;
    private List<Integer> timeSlotIds;
}