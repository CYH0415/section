package com.zju.section.dto;

import lombok.Data;

/**
 * 添加教室请求DTO
 */
@Data
public class AddClassroomRequest {
    private String campus;
    private Integer capacity;
    private String building;
    private Integer roomNumber;
}
