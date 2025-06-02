package com.zju.section.dto;

import lombok.Data;

@Data
public class ApplicationHistoryDetailResponse {
    private Integer secId;
    private String reason;
    private Integer teacherId;
    private String suggestion;
    private Boolean finalDecision;
    private String courseTitle;
    private String classroomLocation;
}
