
USE inforsystem;

CREATE TABLE `student` (
	`user_id` INTEGER NOT NULL,
	`dept_name` VARCHAR(255) NOT NULL,
	`tot_cred` INTEGER NOT NULL,
	PRIMARY KEY(`user_id`)
);


CREATE TABLE `course` (
	`course_id` INTEGER NOT NULL AUTO_INCREMENT UNIQUE,
	`title` VARCHAR(255) NOT NULL,
	`dept_name` VARCHAR(255) NOT NULL,
	`credits` INTEGER NOT NULL,
	`course_introduction` VARCHAR(255) NOT NULL,
	`capacity` INTEGER NOT NULL,
	PRIMARY KEY(`course_id`)
);


CREATE TABLE `department` (
	`dept_name` VARCHAR(255) NOT NULL UNIQUE,
	`Building` VARCHAR(255) NOT NULL,
	PRIMARY KEY(`dept_name`)
);


CREATE TABLE `takes` (
	`student_id` INTEGER NOT NULL,
	`takes_id` INTEGER NOT NULL AUTO_INCREMENT UNIQUE,
	`sec_id` INTEGER NOT NULL,
	PRIMARY KEY(`takes_id`)
);


CREATE TABLE `section` (
	`course_id` INTEGER NOT NULL AUTO_INCREMENT UNIQUE,
	`sec_id` INTEGER NOT NULL,
	`semester` VARCHAR(255) NOT NULL,
	`year` YEAR NOT NULL,
	`classroom_id` INTEGER NOT NULL,
	`time_slot_id` INTEGER,
	`teacher_id` INTEGER NOT NULL,
	PRIMARY KEY(`sec_id`)
);


CREATE TABLE `classroom` (
	`campus` VARCHAR(255) NOT NULL UNIQUE,
	`room_number` INTEGER NOT NULL,
	`capacity` INTEGER NOT NULL,
	`classroom_id` INTEGER NOT NULL AUTO_INCREMENT UNIQUE,
	`building` VARCHAR(255),
	PRIMARY KEY(`classroom_id`)
);


CREATE TABLE `teacher` (
	`user_id` INTEGER NOT NULL,
	`dept_name` VARCHAR(255) NOT NULL,
	`salary` INTEGER NOT NULL,
	PRIMARY KEY(`user_id`)
);


CREATE TABLE `time_slot` (
	`time_slot_id` INTEGER NOT NULL AUTO_INCREMENT UNIQUE,
	`day` INTEGER NOT NULL,
	`start_time` TIME NOT NULL,
	`end_time` TIME NOT NULL,
	PRIMARY KEY(`time_slot_id`)
);


CREATE TABLE `administrator` (
	`user_id` INTEGER NOT NULL,
	PRIMARY KEY(`user_id`)
);


CREATE TABLE `grade_change` (
	`takes_id` INTEGER NOT NULL,
	`teacher_id` INTEGER NOT NULL,
	`result` BOOLEAN,
	`new_grade` INTEGER NOT NULL,
	`apply_time` DATETIME NOT NULL,
	`check_time` DATETIME,
	`grade_type` VARCHAR(255) NOT NULL,
	`grade_change_id` INTEGER NOT NULL AUTO_INCREMENT UNIQUE,
	PRIMARY KEY(`grade_change_id`)
);


CREATE TABLE `user` (
	`user_id` INTEGER NOT NULL AUTO_INCREMENT UNIQUE,
	`account_number` VARCHAR(255) NOT NULL,
	`password` VARCHAR(255) NOT NULL,
	`personal_infor_id` INTEGER NOT NULL,
	`type` ENUM('student', 'teacher', 'administrator') NOT NULL,
	PRIMARY KEY(`user_id`)
);


CREATE TABLE `grade` (
	`takes_id` INTEGER NOT NULL,
	`grade` INTEGER NOT NULL,
	`proportion` FLOAT NOT NULL,
	`type` ENUM('attending', 'homework', 'test') NOT NULL,
	`grade_id` INTEGER NOT NULL AUTO_INCREMENT UNIQUE,
	PRIMARY KEY(`grade_id`)
);


CREATE TABLE `personal_information` (
	`personal_infor_id` INTEGER NOT NULL AUTO_INCREMENT UNIQUE,
	`name` VARCHAR(255) NOT NULL,
	`phone_number` VARCHAR(255) NOT NULL,
	`picture` VARCHAR(255) NOT NULL,
	PRIMARY KEY(`personal_infor_id`)
);


CREATE TABLE `application` (
	`sec_id` INTEGER NOT NULL,
	`reason` VARCHAR(256) NOT NULL,
	`teacher` VARCHAR(256) NOT NULL,
	`suggestion` VARCHAR(256),
	`final` BOOLEAN NOT NULL,
	PRIMARY KEY(`sec_id`)
);


ALTER TABLE student ADD FOREIGN KEY (dept_name) REFERENCES department(dept_name);
ALTER TABLE takes ADD FOREIGN KEY (student_id) REFERENCES student(user_id);
ALTER TABLE section ADD FOREIGN KEY (teacher_id) REFERENCES teacher(user_id);
ALTER TABLE `course`
ADD FOREIGN KEY(`dept_name`) REFERENCES `department`(`dept_name`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `takes`
ADD FOREIGN KEY(`sec_id`) REFERENCES `section`(`sec_id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `section`
ADD FOREIGN KEY(`course_id`) REFERENCES `course`(`course_id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `section`
ADD FOREIGN KEY(`time_slot_id`) REFERENCES `time_slot`(`time_slot_id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `teacher`
ADD FOREIGN KEY(`dept_name`) REFERENCES `department`(`dept_name`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `student`
ADD FOREIGN KEY(`dept_name`) REFERENCES `department`(`dept_name`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `grade_change`
ADD FOREIGN KEY(`takes_id`) REFERENCES `takes`(`takes_id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `student`
ADD FOREIGN KEY(`user_id`) REFERENCES `user`(`user_id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `teacher`
ADD FOREIGN KEY(`user_id`) REFERENCES `user`(`user_id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `administrator`
ADD FOREIGN KEY(`user_id`) REFERENCES `user`(`user_id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `section`
ADD FOREIGN KEY(`classroom_id`) REFERENCES `classroom`(`classroom_id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `grade`
ADD FOREIGN KEY(`takes_id`) REFERENCES `takes`(`takes_id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `user`
ADD FOREIGN KEY(`personal_infor_id`) REFERENCES `personal_information`(`personal_infor_id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `takes`
ADD FOREIGN KEY(`student_id`) REFERENCES `student`(`user_id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `section`
ADD FOREIGN KEY(`teacher_id`) REFERENCES `teacher`(`user_id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `grade_change`
ADD FOREIGN KEY(`teacher_id`) REFERENCES `teacher`(`user_id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE `application`
ADD FOREIGN KEY(`sec_id`) REFERENCES `section`(`sec_id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE grade
ADD CONSTRAINT chk_grade_range 
CHECK (grade BETWEEN 0 AND 100);

ALTER TABLE grade
MODIFY COLUMN proportion DECIMAL(3,2),
ADD CONSTRAINT chk_proportion_range 
CHECK (proportion BETWEEN 0 AND 1);

CREATE INDEX idx_takes_student ON takes(student_id);
CREATE INDEX idx_section_course ON section(course_id);
CREATE INDEX idx_grade_take ON grade(takes_id);

CREATE INDEX idx_section_search ON section(semester, year);
CREATE INDEX idx_grade_change_search ON grade_change(takes_id, check_time);
CREATE INDEX idx_application_section ON application(sec_id);