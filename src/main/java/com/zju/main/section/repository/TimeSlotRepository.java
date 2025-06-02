package com.zju.main.section.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zju.main.section.entity.TimeSlot;

import java.util.List;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Integer> {
    
    /**
     * 根据星期几查询时间段
     * @param day 星期几
     * @return 时间段列表
     */
    List<TimeSlot> findByDay(Integer day);
}