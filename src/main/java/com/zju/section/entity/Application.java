package com.zju.section.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 申请实体类
 */
@Entity
@Table(name = "application")
@Data
public class Application {
    
    @Id
    @Column(name = "sec_id")
    private Integer secId;
      @Column(name = "reason", nullable = false, length = 256)
    private String reason;
    
    @Column(name = "teacher_id", nullable = false)
    private Integer teacherId;
    
    @Column(name = "suggestion", length = 256)
    private String suggestion;
    
    @Column(name = "final")
    private Boolean finalDecision;
    
    @OneToOne
    @JoinColumn(name = "sec_id", insertable = false, updatable = false)
    private Section section;
}