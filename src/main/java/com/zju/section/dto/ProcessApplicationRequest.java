package com.zju.section.dto;

import lombok.Data;

/**
 * 处理申请请求DTO
 */
@Data
public class ProcessApplicationRequest {
    private Integer secId;
    private String suggestion;
    private Boolean finalDecision;
}
