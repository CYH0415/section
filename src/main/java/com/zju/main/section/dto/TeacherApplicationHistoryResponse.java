package com.zju.main.section.dto;

import lombok.Data;
import org.springframework.data.domain.Page;

import com.zju.main.section.entity.Application;

import java.util.List;

/**
 * 教师申请历史查询响应DTO
 */
@Data
public class TeacherApplicationHistoryResponse {
    private long total;
    private List<Application> items;

    public TeacherApplicationHistoryResponse(Page<Application> page) {
        this.total = page.getTotalElements();
        this.items = page.getContent();
    }
    
    public TeacherApplicationHistoryResponse(List<Application> items) {
        this.total = items.size();
        this.items = items;
    }
}
