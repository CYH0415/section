package com.zju.section.dto;

import lombok.Data;

/**
 * 排课请求DTO
 */
@Data
public class ScheduleRequest {
    private Integer sectionId;
    private Integer classroomId;
    private Integer timeSlotId;
}
