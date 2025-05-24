package com.zju.section.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zju.section.common.ApiResult;
import com.zju.section.entity.Classroom;
import com.zju.section.entity.Section;
import com.zju.section.repository.ClassroomRepository;
import com.zju.section.repository.SectionRepository;

import java.util.List;
import java.util.Optional;

/**
 * 教室管理服务类
 */
@Service
public class ClassroomManager {
    
    @Autowired
    private ClassroomRepository classroomRepository;
    
    @Autowired
    private SectionRepository sectionRepository;
    
    /**
     * 添加教室
     * 通过校区/容量/地址/楼宇信息创建新教室
     * 
     * @param campus 校区
     * @param capacity 容量
     * @param building 楼宇
     * @param roomNumber 房间号
     * @return 添加结果
     */
    @Transactional
    public ApiResult<?> add_classroom(String campus, Integer capacity, String building, Integer roomNumber) {
        // 检查参数
        if (campus == null || campus.trim().isEmpty()) {
            return ApiResult.error("校区不能为空");
        }
        
        if (capacity == null || capacity <= 0) {
            return ApiResult.error("容量必须大于0");
        }
        
        if (roomNumber == null || roomNumber <= 0) {
            return ApiResult.error("房间号必须大于0");
        }
        
        // 检查教室是否已存在
        Classroom existingClassroom = classroomRepository.findByCampusAndRoomNumber(campus, roomNumber);
        if (existingClassroom != null) {
            return ApiResult.error("该教室已存在");
        }
        
        // 创建新教室
        Classroom classroom = new Classroom();
        classroom.setCampus(campus);
        classroom.setCapacity(capacity);
        classroom.setBuilding(building);
        classroom.setRoomNumber(roomNumber);
        
        // 保存教室
        classroom = classroomRepository.save(classroom);
        
        return ApiResult.success("添加教室成功", classroom);
    }
    
    /**
     * 修改教室信息
     * 修改指定教室的可选属性（至少更新一个字段）
     * 
     * @param classroomId 教室ID
     * @param newCampus 新校区（可选）
     * @param newCapacity 新容量（可选）
     * @param newBuilding 新楼宇（可选）
     * @return 修改结果
     */
    @Transactional
    public ApiResult<?> modify_classroom(Integer classroomId, String newCampus, Integer newCapacity, String newBuilding) {
        // 检查教室是否存在
        Optional<Classroom> optionalClassroom = classroomRepository.findById(classroomId);
        if (!optionalClassroom.isPresent()) {
            return ApiResult.error("教室不存在");
        }
        
        Classroom classroom = optionalClassroom.get();
        boolean changed = false;
        
        // 更新校区
        if (newCampus != null && !newCampus.trim().isEmpty()) {
            classroom.setCampus(newCampus);
            changed = true;
        }
        
        // 更新容量
        if (newCapacity != null && newCapacity > 0) {
            classroom.setCapacity(newCapacity);
            changed = true;
        }
        
        // 更新楼宇
        if (newBuilding != null) {
            classroom.setBuilding(newBuilding);
            changed = true;
        }
        
        if (!changed) {
            return ApiResult.error("未提供任何要修改的字段");
        }
        
        // 保存更新后的教室
        classroom = classroomRepository.save(classroom);
        
        return ApiResult.success("修改教室成功", classroom);
    }
    
    /**
     * 查询教室
     * 根据教室ID/地址/楼宇名称进行模糊查询
     * 
     * @param keyword 关键字
     * @return 查询结果
     */
    public ApiResult<?> query_classroom(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return ApiResult.error("关键字不能为空");
        }
        
        List<Classroom> classrooms = classroomRepository.findByKeyword(keyword);
        
        if (classrooms.isEmpty()) {
            return ApiResult.success("未找到相关教室", classrooms);
        }
        
        return ApiResult.success("查询教室成功", classrooms);
    }
    
    /**
     * 删除教室
     * 永久删除指定教室记录
     * 
     * @param classroomId 教室ID
     * @return 删除结果
     */
    @Transactional
    public ApiResult<?> delete_classroom(Integer classroomId) {
        // 检查教室是否存在
        Optional<Classroom> optionalClassroom = classroomRepository.findById(classroomId);
        if (!optionalClassroom.isPresent()) {
            return ApiResult.error("教室不存在");
        }
        
        // 检查教室是否被使用
        List<Section> sections = sectionRepository.findByClassroomId(classroomId);
        if (!sections.isEmpty()) {
            return ApiResult.error("该教室正在被使用，无法删除");
        }
        
        // 删除教室
        classroomRepository.deleteById(classroomId);
        
        return ApiResult.success("删除教室成功");
    }
}
