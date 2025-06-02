package com.zju.main.section.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.Year;
import java.util.List;

/**
 * Section详情响应DTO，包含联表查询的详细信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SectionDetailResponse {
    // Section基本信息
    private Integer secId;
    private Integer courseId;
    private String semester;
    private Year year;
    private Integer classroomId;
    private List<Integer> timeSlotIds;
    private Integer teacherId;
    
    // 联表查询的额外信息
    private String courseTitle;         // 课程名称
    private String teacherName;         // 教师姓名
    private String classroomLocation;   // 教室位置 (campus-building-roomNumber)
    private String classroomBuilding;   // 教室楼宇
    private String classroomCampus;     // 教室校区
    private Integer roomNumber;         // 房间号
}