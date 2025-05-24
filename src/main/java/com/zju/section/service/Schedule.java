package com.zju.section.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zju.section.common.ApiResult;
import com.zju.section.entity.Section;
import com.zju.section.entity.Classroom;
import com.zju.section.entity.TimeSlot;
import com.zju.section.repository.SectionRepository;
import com.zju.section.repository.ClassroomRepository;
import com.zju.section.repository.TimeSlotRepository;

import java.util.Optional;

/**
 * 排课服务类
 */
@Service
public class Schedule {
    
    @Autowired
    private SectionRepository sectionRepository;
    
    @Autowired
    private ClassroomRepository classroomRepository;
    
    @Autowired
    private TimeSlotRepository timeSlotRepository;
    
    /**
     * 自动排课方法
     * 对已经分配了老师、课程和学年的section进行分配教室和时间段
     * 
     * @return 排课结果
     */
    public ApiResult<?> auto_schedule() {
        // 自动排课实现（暂不实现）
        return ApiResult.success("自动排课功能暂未实现");
    }
    
    /**
     * 手动排课方法
     * 对已经分配了老师、课程和学年的section进行手动分配教室和时间段
     * 
     * @param sectionId 课程章节ID
     * @param classroomId 教室ID
     * @param timeSlotId 时间段ID
     * @return 排课结果
     */
    @Transactional
    public ApiResult<?> modify_schedule(Integer sectionId, Integer classroomId, Integer timeSlotId) {
        // 检查section是否存在
        Optional<Section> optionalSection = sectionRepository.findById(sectionId);
        if (!optionalSection.isPresent()) {
            return ApiResult.error("课程章节不存在，排课失败");
        }
        
        // 检查classroom是否存在
        Optional<Classroom> optionalClassroom = classroomRepository.findById(classroomId);
        if (!optionalClassroom.isPresent()) {
            return ApiResult.error("教室不存在，排课失败");
        }
        
        // 检查timeSlot是否存在
        Optional<TimeSlot> optionalTimeSlot = timeSlotRepository.findById(timeSlotId);
        if (!optionalTimeSlot.isPresent()) {
            return ApiResult.error("时间段不存在，排课失败");
        }
        
        // 更新section的教室和时间段
        Section section = optionalSection.get();
        section.setClassroomId(classroomId);
        section.setTimeSlotId(timeSlotId);
        
        // 保存更新后的section
        sectionRepository.save(section);
        
        return ApiResult.success("手动排课成功");
    }
}
