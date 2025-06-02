package com.zju.main.section.dto;

import lombok.Data;
import org.springframework.data.domain.Page;

import com.zju.main.entity.Application;

import java.util.List;

@Data
public class QueryApplicationResponse {
    private long total;
    private List<Application> items;

    public QueryApplicationResponse(Page<Application> page) {
        this.total = page.getTotalElements();
        this.items = page.getContent();
    }
}