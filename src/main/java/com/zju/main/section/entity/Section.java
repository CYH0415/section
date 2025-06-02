package com.zju.main.section.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Year;
import java.util.List;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import org.hibernate.annotations.Type;
//import org.hibernate.annotations.TypeDef;

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

    @Type(JsonStringType.class)
    @Column(name = "time_slot_ids", columnDefinition = "json")
    private List<Integer> timeSlotIds;
    
    @Column(name = "teacher_id", nullable = false)
    private Integer teacherId;
    
    @ManyToOne
    @JoinColumn(name = "classroom_id", insertable = false, updatable = false)
    private Classroom classroom;
    
//    @ManyToOne
//    @JoinColumn(name = "time_slot_id", insertable = false, updatable = false)
//    private TimeSlot timeSlot;

    @Override
    public String toString() {
        return "Section{" +
                "secId=" + secId +
                ", courseId=" + courseId +
                ", classroomId=" + classroomId +
                ", timeSlotIds=" + timeSlotIds +
                // 如果有其它字段也要加上
                '}';
    }
}