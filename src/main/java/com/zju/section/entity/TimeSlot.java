package com.zju.section.entity;

import jakarta.persistence.*;
import java.time.LocalTime;
import lombok.Data;

/**
 * 时间段实体类
 */
@Entity
@Table(name = "time_slot")
@Data
public class TimeSlot {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "time_slot_id")
    private Integer timeSlotId;
    
    @Column(name = "day", nullable = false)
    private Integer day;
    
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;
    
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;


}