package com.zju.main.section.dto;

import lombok.Data;

/**
 * 处理申请请求DTO
 */
@Data
public class ProcessApplicationRequest {
    private Integer appId;
    private Integer adminId;
    private String suggestion;
    private Boolean finalDecision;
}