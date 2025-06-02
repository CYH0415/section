package com.zju.section.dto;

import lombok.Data;

/**
 * 添加申请请求DTO
 */
@Data
public class AddApplicationRequest {
    private Integer secId;
    private String reason;
    private Integer teacherId;
}