package com.zju.main.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zju.main.section.service.ApplicationManager;
import com.zju.main.section.service.ClassroomManager;
import com.zju.main.section.service.Schedule;
import com.zju.main.section.service.Search;

/**
 * 服务配置类
 */
@Configuration
public class ServiceConfig {
    
    @Bean
    public Schedule scheduleService() {
        return new Schedule();
    }
    
    @Bean
    public Search searchService() {
        return new Search();
    }
    
    @Bean
    public ClassroomManager classroomManager() {
        return new ClassroomManager();
    }
    
    @Bean
    public ApplicationManager applicationManager() {
        return new ApplicationManager();
    }
}