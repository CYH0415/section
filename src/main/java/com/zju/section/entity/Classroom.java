package com.zju.section.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 教室实体类
 */
@Entity
@Table(name = "classroom")
@Data
public class Classroom {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "classroom_id")
    private Integer classroomId;
    
    @Column(name = "campus", nullable = false, unique = true)
    private String campus;
    
    @Column(name = "room_number", nullable = false)
    private Integer roomNumber;
    
    @Column(name = "capacity", nullable = false)
    private Integer capacity;
    
    @Column(name = "building")
    private String building;

    @Column(name="type")
    private String type;
}
