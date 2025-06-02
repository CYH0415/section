package com.zju.main.section.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.zju.main.entity.Classroom;

import java.util.List;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Integer> {    
    /**
     * 根据关键字查询教室
     * @param keyword 关键字
     * @return 教室列表
     */    
    @Query("SELECT c FROM Classroom c WHERE " +
           "c.campus LIKE CONCAT('%', :keyword, '%') OR " +
           "c.building LIKE CONCAT('%', :keyword, '%')")
    List<Classroom> findByKeyword(@Param("keyword") String keyword);
    
    /**
     * 根据ID查询教室
     * @param id 教室ID
     * @return 教室列表
     */
    List<Classroom> findByClassroomId(Integer id);
    
    /**
     * 根据校区和房间号查询教室
     * @param campus 校区
     * @param roomNumber 房间号
     * @return 教室
     */
    Classroom findByCampusAndRoomNumber(String campus, Integer roomNumber);
        /**
         * 查出指定院系（deptName）所在校区的所有教室
         */
        @Query(
                value = "SELECT r.* "
                        + "  FROM classroom r "
                        + "  JOIN department d "
                        + "    ON r.campus = d.campus "
                        + " WHERE d.dept_name = :deptName",
                nativeQuery = true
        )
        List<Classroom> findAllByDeptName(@Param("deptName") String deptName);


}