package com.zju.section.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zju.section.entity.Application;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Integer> {
    
    /**
     * 根据课程章节ID查询申请
     * @param secId 课程章节ID
     * @return 申请
     */
    Application findBySecId(Integer secId);
}
