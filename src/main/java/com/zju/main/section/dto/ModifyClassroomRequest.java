package com.zju.main.section.dto;

import lombok.Data;

/**
 * 修改教室请求DTO
 */
@Data
public class ModifyClassroomRequest {
    private Integer classroomId;
    private String newCampus;
    private Integer newCapacity;
    private String newBuilding;
}