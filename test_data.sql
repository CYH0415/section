
use inforsystem;
-- 1. 插入院系信息（基础数据）
INSERT INTO department (dept_name, campus) VALUES
('Computer Science', 'Main Campus'),
('Mathematics', 'North Campus'),
('Physics', 'East Campus');

-- 2. 插入个人信息（用户基础）
INSERT INTO personal_information (name, phone_number, picture) VALUES
('张三', '138-1234-5678', 'zhangsan.jpg'),
('李四', '139-8765-4321', 'lisi.jpg'),
('王教授', '136-1122-3344', 'wang.jpg'),
('管理员', '158-5544-3322', 'admin.jpg');

-- 3. 创建用户账户（所有角色基础）
INSERT INTO user (account_number, password, personal_infor_id, type) VALUES
('s1001', 'student123', 1, 'student'),
('t2001', 'teacher123', 3, 'teacher'),
('admin01', 'admin123', 4, 'administrator'),
('s1002', 'student456', 2, 'student');

-- 4. 创建学生记录
INSERT INTO student (user_id, dept_name, tot_cred) VALUES
(1, 'Computer Science', 96),
(4, 'Mathematics', 88);

-- 5. 创建教师记录
INSERT INTO teacher (user_id, dept_name) VALUES
(2, 'Computer Science');

-- 6. 创建管理员
INSERT INTO administrator (user_id) VALUES (3);

-- 7. 插入教室数据
INSERT INTO classroom (campus, room_number, capacity, building, type) VALUES
('Main Campus', 301, 60, 'Science Building', 'Lecture Hall'),
('North Campus', 202, 40, 'Math Building', 'Lab'),
('East Campus', 101, 100, 'Main Building', 'Auditorium');

-- 8. 创建课程数据
INSERT INTO course (title, dept_name, credits, course_introduction, capacity, required_room_type, grade_year, period) VALUES
('数据库系统', 'Computer Science', 3, '关系型数据库原理与应用', 50, 'Lecture Hall', 3, 2),
('高等数学', 'Mathematics', 4, '微积分与线性代数', 80, 'Lecture Hall', 1, 1),
('量子力学', 'Physics', 3, '基础量子理论', 40, 'Lab', 4, 2);

-- 9. 创建教学班（section）
INSERT INTO section (course_id, semester, year, classroom_id, time_slot_ids, teacher_id) VALUES
(1, 'Fall', 2023, 1, '["Mon 8:00-9:40", "Wed 8:00-9:40"]', 2),
(2, 'Spring', 2024, 2, '["Tue 10:00-11:40", "Thu 10:00-11:40"]', 2),
(3, 'Fall', 2023, 3, '["Fri 14:00-17:00"]', 2);

-- 10. 学生选课记录
INSERT INTO takes (student_id, sec_id) VALUES
(1, 1),
(1, 2),
(4, 3);

-- 11. 成绩记录
INSERT INTO grade (takes_id, grade, proportion, type) VALUES
(1, 85, 0.3, 'test'),
(1, 90, 0.7, 'homework'),
(2, 78, 1.0, 'attending'),
(3, 92, 0.5, 'test');

-- 12. 成绩变更申请
INSERT INTO grade_change (takes_id, teacher_id, result, new_grade, apply_time, check_time, grade_type) VALUES
(1, 2, TRUE, 88, '2023-12-01 14:30:00', '2023-12-02 10:15:00', 'recheck'),
(3, 2, NULL, 95, '2023-12-05 09:00:00', NULL, 'adjustment');

-- 13. 课程申请记录
INSERT INTO application (sec_id, reason, teacher, suggestion, final) VALUES
(2, '更换更大教室', '王教授', '已批准', TRUE),
(3, '实验设备升级', '李教授', '待处理', FALSE);