package com.zju.section.dto;

import com.zju.section.entity.Application;
import lombok.Data;
import org.springframework.data.domain.Page;

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