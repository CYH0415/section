package com.zju.section.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 课程实体，对应数据库表 course
 */
@Entity
@Table(name = "course")
@Data
@NoArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Integer courseId;

    /** 课程标题 */
    @Column(name = "title", nullable = false)
    private String title;

    /** 院系名称 */
    @Column(name = "dept_name", nullable = false)
    private String deptName;

    /** 学分／学时 */
    @Column(name = "credits", nullable = false)
    private Integer credits;

    /** 课程简介 */
    @Column(name = "course_introduction", nullable = false)
    private String courseIntroduction;

    /** 最大选课容量 */
    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    /**
     * 所需教室类型，例如 "LAB", "LECTURE" 等
     * 对应表中 required_room_type 字段
     */
    @Column(name = "required_room_type", nullable = false)
    private String requiredRoomType;
    @Column(name = "grade_year", nullable = false)
    private Integer gradeYear;

    /**
     * 课程课时，表示该课程的总课时数
     */
    @Column(name = "period", nullable = false)
    private Integer period;
}