package com.zju.main.section.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zju.main.section.common.ApiResult;
import com.zju.main.section.entity.Classroom;
import com.zju.main.section.entity.Section;
import com.zju.main.section.repository.ClassroomRepository;
import com.zju.main.section.repository.SectionRepository;

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
     */
    @Transactional
    public ApiResult<?> add_classroom(String campus, Integer capacity, String building, Integer roomNumber) {
        if (campus == null || campus.trim().isEmpty()) {
            return ApiResult.error("校区不能为空");
        }

        if (capacity == null || capacity <= 0) {
            return ApiResult.error("容量必须大于0");
        }

        if (roomNumber == null || roomNumber <= 0) {
            return ApiResult.error("房间号必须大于0");
        }

        Classroom existingClassroom = classroomRepository.findByCampusAndRoomNumber(campus, roomNumber);
        if (existingClassroom != null) {
            return ApiResult.error("该教室已存在");
        }

        Classroom classroom = new Classroom();
        classroom.setCampus(campus);
        classroom.setCapacity(capacity);
        classroom.setBuilding(building);
        classroom.setRoomNumber(roomNumber);

        classroom = classroomRepository.save(classroom);

        return ApiResult.success("添加教室成功", classroom);
    }

    /**
     * 修改教室信息
     */
    @Transactional
    public ApiResult<?> modify_classroom(Integer classroomId, String newCampus, Integer newCapacity, String newBuilding) {
        Optional<Classroom> optionalClassroom = classroomRepository.findById(classroomId);
        if (!optionalClassroom.isPresent()) {
            return ApiResult.error("教室不存在");
        }

        Classroom classroom = optionalClassroom.get();
        boolean changed = false;

        if (newCampus != null && !newCampus.trim().isEmpty()) {
            classroom.setCampus(newCampus);
            changed = true;
        }

        if (newCapacity != null && newCapacity > 0) {
            classroom.setCapacity(newCapacity);
            changed = true;
        }

        if (newBuilding != null) {
            classroom.setBuilding(newBuilding);
            changed = true;
        }

        if (!changed) {
            return ApiResult.error("未提供任何要修改的字段");
        }

        classroom = classroomRepository.save(classroom);

        return ApiResult.success("修改教室成功", classroom);
    }

    /**
     * 查询教室
     * keyword 为空、null 或 "undefined" 时返回所有教室
     */
    public ApiResult<?> query_classroom(String keyword) {
        List<Classroom> classrooms;

        if (keyword == null || keyword.trim().isEmpty() || "undefined".equalsIgnoreCase(keyword)) {
            classrooms = classroomRepository.findAll();
            return ApiResult.success("查询所有教室成功", classrooms);
        }

        classrooms = classroomRepository.findByKeyword(keyword);

        if (classrooms.isEmpty()) {
            return ApiResult.success("未找到相关教室", classrooms);
        }

        return ApiResult.success("查询教室成功", classrooms);
    }

    /**
     * 删除教室
     */
    @Transactional
    public ApiResult<?> delete_classroom(Integer classroomId) {
        Optional<Classroom> optionalClassroom = classroomRepository.findById(classroomId);
        if (!optionalClassroom.isPresent()) {
            return ApiResult.error("教室不存在");
        }

        List<Section> sections = sectionRepository.findByClassroomId(classroomId);
        if (!sections.isEmpty()) {
            return ApiResult.error("该教室正在被使用，无法删除");
        }

        classroomRepository.deleteById(classroomId);

        return ApiResult.success("删除教室成功");
    }
}