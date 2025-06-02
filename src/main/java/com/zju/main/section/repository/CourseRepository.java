package com.zju.main.section.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zju.main.entity.Course;

import java.util.Collection;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

    /**
     * 批量根据课程ID查询（避免在排课时 N+1 次调用 findById）
     *
     * @param courseIds 课程ID 列表
     * @return 对应的课程列表
     */
    List<Course> findAllByCourseIdIn(Collection<Integer> courseIds);

    /**
     * 根据院系查询课程（如果你在系统其他地方会用到）
     *
     * @param deptName 院系名称
     * @return 该院系下的课程列表
     */
    List<Course> findByDeptName(String deptName);

    /**
     * 根据课程标题模糊查询（系统查询功能常用）
     *
     * @param keyword 标题关键字
     * @return 包含关键字的课程列表
     */
    List<Course> findByTitleContaining(String keyword);

    /**
     * 根据学分范围查询（如果需要按学时筛选课程）
     *
     * @param minCredits 最低学分
     * @param maxCredits 最高学分
     * @return 学分在给定范围内的课程列表
     */
    List<Course> findByCreditsBetween(int minCredits, int maxCredits);
}