package com.zju.section.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Year;
import java.util.List;

/**
 * 课程章节实体类
 */
@Entity
@Table(name = "section")
@Data
public class Section {
    
    @Id
    @Column(name = "sec_id")
    private Integer secId;
    
    @Column(name = "course_id", nullable = false)
    private Integer courseId;
    
    @Column(name = "semester", nullable = false)
    private String semester;
    
    @Column(name = "year", nullable = false)
    private Year year;
    
    @Column(name = "classroom_id", nullable = false)
    private Integer classroomId;
    
    @Column(name = "time_slot_id")
    private List<Integer> timeSlotIds;
    
    @Column(name = "teacher_id", nullable = false)
    private Integer teacherId;
    
    @ManyToOne
    @JoinColumn(name = "classroom_id", insertable = false, updatable = false)
    private Classroom classroom;
    
    @ManyToOne
    @JoinColumn(name = "time_slot_id", insertable = false, updatable = false)
    private TimeSlot timeSlot;
}
