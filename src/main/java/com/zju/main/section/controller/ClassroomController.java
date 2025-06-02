package com.zju.main.section.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zju.main.section.common.ApiResult;
import com.zju.main.section.dto.AddClassroomRequest;
import com.zju.main.section.dto.ModifyClassroomRequest;
import com.zju.main.section.service.ClassroomManager;

/**
 * 教室管理控制器
 */
@RestController
@RequestMapping("/api/classroom")
public class ClassroomController {
    
    @Autowired
    private ClassroomManager classroomManager;
      /**
     * 添加教室
     */
    @PostMapping("/add")
    public ApiResult<?> addClassroom(@RequestBody AddClassroomRequest request) {
        return classroomManager.add_classroom(
            request.getCampus(),
            request.getCapacity(),
            request.getBuilding(),
            request.getRoomNumber()
        );
    }
      /**
     * 修改教室
     */
    @PutMapping("/modify")
    public ApiResult<?> modifyClassroom(@RequestBody ModifyClassroomRequest request) {
        return classroomManager.modify_classroom(
            request.getClassroomId(), 
            request.getNewCampus(), 
            request.getNewCapacity(), 
            request.getNewBuilding()
        );
    }
    
    /**
     * 查询教室
     */
    @GetMapping("/query")
    public ApiResult<?> queryClassroom(@RequestParam String keyword) {
        return classroomManager.query_classroom(keyword);
    }
    
    /**
     * 删除教室
     */
    @DeleteMapping("/delete")
    public ApiResult<?> deleteClassroom(@RequestParam Integer classroomId) {
        return classroomManager.delete_classroom(classroomId);
    }
}