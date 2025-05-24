package com.zju.section.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zju.section.service.ApplicationManager;
import com.zju.section.service.ClassroomManager;
import com.zju.section.service.Schedule;
import com.zju.section.service.Search;

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
