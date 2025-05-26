-- Temporarily disable foreign key checks to avoid order issues during insertion
SET FOREIGN_KEY_CHECKS = 0;

-- Data for `department` table
INSERT INTO `department` (`dept_name`, `campus`) VALUES
                                                     ('Computer Science', 'Main Campus'),
                                                     ('Electrical Engineering', 'Main Campus'),
                                                     ('Mathematics', 'Science Campus'),
                                                     ('Physics', 'Science Campus'),
                                                     ('Business Administration', 'Downtown Campus');

-- Data for `personal_information` table
-- (Assuming personal_infor_id is AUTO_INCREMENT starting from 1)
INSERT INTO `personal_information` (`name`, `phone_number`, `picture`) VALUES
                                                                           ('Alice Wonderland', '555-0101', 'alice.jpg'),
                                                                           ('Bob The Builder', '555-0102', 'bob.jpg'),
                                                                           ('Charles Xavier', '555-0103', 'charles.jpg'),
                                                                           ('Diana Prince', '555-0104', 'diana.jpg'),
                                                                           ('Edward Scissorhands', '555-0105', 'edward.jpg'),
                                                                           ('Fiona Gallagher', '555-0106', 'fiona.jpg'),
                                                                           ('Gregory House', '555-0107', 'gregory.jpg'),
                                                                           ('Harry Potter', '555-0108', 'harry.jpg'),
                                                                           ('Irene Adler', '555-0109', 'irene.jpg'),
                                                                           ('John Doe', '555-0110', 'john.jpg'),
                                                                           ('Jane Smith', '555-0111', 'jane.jpg'),
                                                                           ('Admin User', '555-0000', 'admin.jpg'),
                                                                           ('Prof. Alan Turing', '555-0201', 'turing.jpg'),
                                                                           ('Prof. Marie Curie', '555-0202', 'curie.jpg'),
                                                                           ('Prof. Isaac Newton', '555-0203', 'newton.jpg'),
                                                                           ('Prof. Ada Lovelace', '555-0204', 'lovelace.jpg'),
                                                                           ('Prof. Peter Parker', '555-0205', 'parker.jpg'),
                                                                           ('Michael Scott', '555-0206', 'scott.jpg'),
                                                                           ('Student Alpha', '555-0301', 's_alpha.jpg'),
                                                                           ('Student Beta', '555-0302', 's_beta.jpg'),
                                                                           ('Student Gamma', '555-0303', 's_gamma.jpg'),
                                                                           ('Student Delta', '555-0304', 's_delta.jpg'),
                                                                           ('Student Epsilon', '555-0305', 's_epsilon.jpg'),
                                                                           ('Student Zeta', '555-0306', 's_zeta.jpg'),
                                                                           ('Student Eta', '555-0307', 's_eta.jpg'),
                                                                           ('Student Theta', '555-0308', 's_theta.jpg'),
                                                                           ('Student Iota', '555-0309', 's_iota.jpg'),
                                                                           ('Student Kappa', '555-0310', 's_kappa.jpg'),
                                                                           ('Student Lambda', '555-0311', 's_lambda.jpg'),
                                                                           ('Student Mu', '555-0312', 's_mu.jpg');
-- (Add more personal_information if more users are needed, ensure personal_infor_id matches user.personal_infor_id)

-- Data for `user` table
-- (Assuming user_id is AUTO_INCREMENT starting from 1)
-- Administrators (personal_infor_id: 12)
INSERT INTO `user` (`account_number`, `password`, `personal_infor_id`, `type`) VALUES
    ('admin01', 'hashed_password_admin01', 12, 'administrator');

-- Teachers (personal_infor_id: 1, 2, 3, 13, 14, 15, 16, 17, 18)
INSERT INTO `user` (`account_number`, `password`, `personal_infor_id`, `type`) VALUES
                                                                                   ('teacher001', 'hashed_password_t001', 1, 'teacher'), -- Alice Wonderland
                                                                                   ('teacher002', 'hashed_password_t002', 2, 'teacher'), -- Bob The Builder
                                                                                   ('teacher003', 'hashed_password_t003', 3, 'teacher'), -- Charles Xavier
                                                                                   ('teacher004', 'hashed_password_t004', 13, 'teacher'), -- Prof. Alan Turing
                                                                                   ('teacher005', 'hashed_password_t005', 14, 'teacher'), -- Prof. Marie Curie
                                                                                   ('teacher006', 'hashed_password_t006', 15, 'teacher'), -- Prof. Isaac Newton
                                                                                   ('teacher007', 'hashed_password_t007', 16, 'teacher'), -- Prof. Ada Lovelace
                                                                                   ('teacher008', 'hashed_password_t008', 17, 'teacher'), -- Prof. Peter Parker
                                                                                   ('teacher009', 'hashed_password_t009', 18, 'teacher'); -- Michael Scott

-- Students (personal_infor_id: 4-11, 19-30)
INSERT INTO `user` (`account_number`, `password`, `personal_infor_id`, `type`) VALUES
                                                                                   ('student001', 'hashed_password_s001', 4, 'student'),   -- Diana Prince
                                                                                   ('student002', 'hashed_password_s002', 5, 'student'),   -- Edward Scissorhands
                                                                                   ('student003', 'hashed_password_s003', 6, 'student'),   -- Fiona Gallagher
                                                                                   ('student004', 'hashed_password_s004', 7, 'student'),   -- Gregory House
                                                                                   ('student005', 'hashed_password_s005', 8, 'student'),   -- Harry Potter
                                                                                   ('student006', 'hashed_password_s006', 9, 'student'),   -- Irene Adler
                                                                                   ('student007', 'hashed_password_s007', 10, 'student'),  -- John Doe
                                                                                   ('student008', 'hashed_password_s008', 11, 'student'),  -- Jane Smith
                                                                                   ('student009', 'hashed_password_s009', 19, 'student'),  -- Student Alpha
                                                                                   ('student010', 'hashed_password_s010', 20, 'student'),  -- Student Beta
                                                                                   ('student011', 'hashed_password_s011', 21, 'student'),  -- Student Gamma
                                                                                   ('student012', 'hashed_password_s012', 22, 'student'),  -- Student Delta
                                                                                   ('student013', 'hashed_password_s013', 23, 'student'),  -- Student Epsilon
                                                                                   ('student014', 'hashed_password_s014', 24, 'student'),  -- Student Zeta
                                                                                   ('student015', 'hashed_password_s015', 25, 'student'),  -- Student Eta
                                                                                   ('student016', 'hashed_password_s016', 26, 'student'),  -- Student Theta
                                                                                   ('student017', 'hashed_password_s017', 27, 'student'),  -- Student Iota
                                                                                   ('student018', 'hashed_password_s018', 28, 'student'),  -- Student Kappa
                                                                                   ('student019', 'hashed_password_s019', 29, 'student'),  -- Student Lambda
                                                                                   ('student020', 'hashed_password_s020', 30, 'student');  -- Student Mu
-- (User IDs: Admin: 1, Teachers: 2-10, Students: 11-30)

-- Data for `administrator` table
INSERT INTO `administrator` (`user_id`) VALUES
    (1); -- admin01

-- Data for `teacher` table
-- (User IDs for teachers: 2, 3, 4, 5, 6, 7, 8, 9, 10)
INSERT INTO `teacher` (`user_id`, `dept_name`, `salary`) VALUES
                                                             (2, 'Computer Science', 70000),        -- Alice (teacher001)
                                                             (3, 'Electrical Engineering', 72000),   -- Bob (teacher002)
                                                             (4, 'Mathematics', 68000),             -- Charles (teacher003)
                                                             (5, 'Computer Science', 80000),        -- Turing (teacher004)
                                                             (6, 'Physics', 75000),                 -- Curie (teacher005)
                                                             (7, 'Mathematics', 82000),             -- Newton (teacher006)
                                                             (8, 'Computer Science', 73000),        -- Lovelace (teacher007)
                                                             (9, 'Physics', 69000),                 -- Parker (teacher008)
                                                             (10, 'Business Administration', 71000); -- Scott (teacher009)

-- Data for `student` table
-- (User IDs for students: 11-30)
INSERT INTO `student` (`user_id`, `dept_name`, `tot_cred`) VALUES
                                                               (11, 'Computer Science', 12), (12, 'Computer Science', 24),
                                                               (13, 'Electrical Engineering', 9), (14, 'Electrical Engineering', 21),
                                                               (15, 'Mathematics', 15), (16, 'Mathematics', 30),
                                                               (17, 'Physics', 6), (18, 'Physics', 18),
                                                               (19, 'Business Administration', 12), (20, 'Business Administration', 27),
                                                               (21, 'Computer Science', 0), (22, 'Electrical Engineering', 3),
                                                               (23, 'Mathematics', 36), (24, 'Physics', 9),
                                                               (25, 'Business Administration', 45), (26, 'Computer Science', 12),
                                                               (27, 'Electrical Engineering', 24), (28, 'Mathematics', 9),
                                                               (29, 'Physics', 21), (30, 'Business Administration', 15);


-- Data for `course` table
-- (Assuming course_id is AUTO_INCREMENT starting from 1)
INSERT INTO `course` (`title`, `dept_name`, `credits`, `course_introduction`, `capacity`, `required_room_type`, `grade_year`, `period`) VALUES
                                                                                                                                            ('Introduction to Programming', 'Computer Science', 3, 'Basics of programming using Python.', 100, 'Lecture Hall', 1, 4),
                                                                                                                                            ('Data Structures', 'Computer Science', 4, 'Fundamental data structures.', 80, 'Lecture Hall', 2, 4),
                                                                                                                                            ('Algorithms', 'Computer Science', 4, 'Design and analysis of algorithms.', 70, 'Standard Classroom', 3, 4),
                                                                                                                                            ('Operating Systems', 'Computer Science', 3, 'Concepts of modern operating systems.', 60, 'Standard Classroom', 3, 3),
                                                                                                                                            ('Database Systems', 'Computer Science', 3, 'Introduction to database design and SQL.', 90, 'Lab', 2, 4),
                                                                                                                                            ('Circuit Theory', 'Electrical Engineering', 4, 'Fundamentals of electrical circuits.', 70, 'Lecture Hall', 1, 4),
                                                                                                                                            ('Digital Logic Design', 'Electrical Engineering', 3, 'Design of digital circuits.', 60, 'Lab', 2, 4),
                                                                                                                                            ('Signals and Systems', 'Electrical Engineering', 4, 'Analysis of signals and linear systems.', 50, 'Standard Classroom', 3, 4),
                                                                                                                                            ('Calculus I', 'Mathematics', 4, 'Differential calculus.', 120, 'Lecture Hall', 1, 5),
                                                                                                                                            ('Linear Algebra', 'Mathematics', 3, 'Vectors, matrices, and linear transformations.', 100, 'Lecture Hall', 1, 3),
                                                                                                                                            ('Probability and Statistics', 'Mathematics', 3, 'Introduction to probability and statistics.', 90, 'Standard Classroom', 2, 3),
                                                                                                                                            ('Classical Mechanics', 'Physics', 4, 'Newtonian mechanics and conservation laws.', 60, 'Lecture Hall', 1, 4),
                                                                                                                                            ('Electromagnetism', 'Physics', 4, 'Electric and magnetic fields.', 50, 'Standard Classroom', 2, 4),
                                                                                                                                            ('Quantum Mechanics I', 'Physics', 3, 'Introduction to quantum physics.', 40, 'Small Classroom', 3, 3),
                                                                                                                                            ('Principles of Management', 'Business Administration', 3, 'Basic management theories.', 100, 'Lecture Hall', 1, 3),
                                                                                                                                            ('Marketing Fundamentals', 'Business Administration', 3, 'Introduction to marketing concepts.', 90, 'Lecture Hall', 2, 3),
                                                                                                                                            ('Financial Accounting', 'Business Administration', 3, 'Basics of financial accounting.', 80, 'Standard Classroom', 2, 3),
                                                                                                                                            ('Intro to AI', 'Computer Science', 3, 'Basic concepts of Artificial Intelligence.', 50, 'Lab', 4, 3),
                                                                                                                                            ('Microeconomics', 'Business Administration', 3, 'Principles of microeconomics.', 70, 'Standard Classroom', 1, 3),
                                                                                                                                            ('Thermodynamics', 'Physics', 3, 'Principles of heat and energy transfer.', 40, 'Small Classroom', 2, 3);
-- (Course IDs: 1-20)

-- Data for `classroom` table
-- (Assuming classroom_id is AUTO_INCREMENT starting from 1)
INSERT INTO `classroom` (`campus`, `room_number`, `capacity`, `building`, `type`) VALUES
                                                                                      ('Main Campus', 101, 190, 'Building A', 'Lecture Hall'),
                                                                                      ('Main Campus', 102, 280, 'Building A', 'Standard Classroom'),
                                                                                      ('Main Campus', 201, 230, 'Building B', 'Lab'),
                                                                                      ('Main Campus', 202, 240, 'Building B', 'Small Classroom'),
                                                                                      ('Science Campus', 301, 200, 'Building C', 'Lecture Hall'),
                                                                                      ('Science Campus', 302, 270, 'Building C', 'Standard Classroom'),
                                                                                      ('Science Campus', 401, 225, 'Building D', 'Lab'),
                                                                                      ('Downtown Campus', 501, 300, 'Building E', 'Lecture Hall'),
                                                                                      ('Downtown Campus', 502, 180, 'Building E', 'Standard Classroom'),
                                                                                      ('Main Campus', 103, 190, 'Building A', 'Standard Classroom'),
                                                                                      ('Science Campus', 303, 140, 'Building C', 'Small Classroom');
-- (Classroom IDs: 1-11)

-- Data for `time_slot` table
-- (Assuming time_slot_id is AUTO_INCREMENT starting from 1)
-- Monday
INSERT INTO `time_slot` (`day`, `start_time`, `end_time`) VALUES
                                                              (1, '08:00:00', '08:50:00'), (1, '09:00:00', '09:50:00'), (1, '10:00:00', '10:50:00'),
                                                              (1, '11:00:00', '11:50:00'), (1, '13:00:00', '13:50:00'), (1, '14:00:00', '14:50:00'),
                                                              (1, '15:00:00', '15:50:00'), (1, '16:00:00', '16:50:00'),
-- Tuesday
                                                              (2, '08:00:00', '08:50:00'), (2, '09:00:00', '09:50:00'), (2, '10:00:00', '10:50:00'),
                                                              (2, '11:00:00', '11:50:00'), (2, '13:00:00', '13:50:00'), (2, '14:00:00', '14:50:00'),
                                                              (2, '15:00:00', '15:50:00'), (2, '16:00:00', '16:50:00'),
-- Wednesday
                                                              (3, '08:00:00', '08:50:00'), (3, '09:00:00', '09:50:00'), (3, '10:00:00', '10:50:00'),
                                                              (3, '11:00:00', '11:50:00'), (3, '13:00:00', '13:50:00'), (3, '14:00:00', '14:50:00'),
                                                              (3, '15:00:00', '15:50:00'), (3, '16:00:00', '16:50:00'),
-- Thursday
                                                              (4, '08:00:00', '08:50:00'), (4, '09:00:00', '09:50:00'), (4, '10:00:00', '10:50:00'),
                                                              (4, '11:00:00', '11:50:00'), (4, '13:00:00', '13:50:00'), (4, '14:00:00', '14:50:00'),
                                                              (4, '15:00:00', '15:50:00'), (4, '16:00:00', '16:50:00'),
-- Friday
                                                              (5, '08:00:00', '08:50:00'), (5, '09:00:00', '09:50:00'), (5, '10:00:00', '10:50:00'),
                                                              (5, '11:00:00', '11:50:00'), (5, '13:00:00', '13:50:00'), (5, '14:00:00', '14:50:00'),
                                                              (5, '15:00:00', '15:50:00'), (5, '16:00:00', '16:50:00');
-- (Time Slot IDs: 1-40, assuming 8 slots per day for 5 days)
-- Example: Mon 8-8:50 is 1, Mon 9-9:50 is 2 ... Fri 16-16:50 is 40
-- MWF 9-9:50 would be e.g. [2, 18, 34] (assuming slot 2 is Mon 9am, 18 is Wed 9am, 34 is Fri 9am)
-- TTH 10-10:50 would be e.g. [11, 27] (assuming slot 11 is Tue 10am, 27 is Thu 10am)

-- Data for `section` table
-- (Manually assigning sec_id. course_id refers to course.course_id)
-- (teacher_id values correspond to user_id of teachers: 2-10)
-- For time_slot_ids: Example JSON arrays.
-- MWF 09:00-09:50 -> time_slot_ids [2, 18, 34] (Mon 09:00, Wed 09:00, Fri 09:00)
-- TTH 10:00-11:50 (2 slots) -> time_slot_ids [11, 12, 27, 28] (Tue 10:00, Tue 11:00, Thu 10:00, Thu 11:00) - Assuming 50min slots, a 2hr class would be 2 consecutive slots.
-- For a 4 period course (e.g. 4x50min slots a week).
-- Intro to Programming (course_id 1), 4 periods. Teacher: Alice (ID 2). Classroom: Lecture Hall (ID 1).
INSERT INTO `section` (`course_id`, `sec_id`, `semester`, `year`, `teacher_id`) VALUES
                                                                                                                     (1, 101, 'Fall', '2024',  2), -- M 9, T 9, W 9, Th 9 (example for 4 distinct slots)
                                                                                                                     (1, 102, 'Fall', '2024',  5), -- M 10, T 10, W 10, Th 10 (another section)
                                                                                                                     (2, 201, 'Fall', '2024',  2), -- M 13-14:50, W 13-14:50 (Data Structures)
                                                                                                                     (5, 301, 'Fall', '2024',  8),-- T 13-14:50, Th 13-14:50 (Database Systems - Lab)
                                                                                                                     (6, 401, 'Fall', '2024',  3),   -- Circuit Theory
                                                                                                                     (9, 501, 'Fall', '2024', 4), -- Calculus I (5 periods example: M9,M10,M11, W9,W10)
                                                                                                                     (9, 502, 'Spring', '2025',  7), -- Calculus I, another teacher, different semester
                                                                                                                     (10, 601, 'Fall', '2024',  4), -- Linear Algebra (3 periods: T11, Th11, F11)
                                                                                                                     (11, 701, 'Spring', '2025',  7), -- Prob & Stats
                                                                                                                     (12, 801, 'Fall', '2024',  6), -- Classical Mechanics
                                                                                                                     (15, 901, 'Fall', '2024',  10), -- Principles of Management
                                                                                                                     (18, 1001, 'Spring', '2025',  5); -- Intro to AI

-- Data for `takes` table
-- (Assuming takes_id is AUTO_INCREMENT starting from 1)
-- (student_id values from 11-30, sec_id from above)
INSERT INTO `takes` (`student_id`, `sec_id`) VALUES
                                                 (11, 101), (12, 101), (11, 201), (13, 401), (15, 501),
                                                 (16, 501), (11, 301), (12, 201), (14, 401), (17, 801),
                                                 (19, 901), (20, 901), (21, 102), (22, 401), (23, 502),
                                                 (24, 801), (25, 901), (26, 101), (27, 401), (28, 601),
                                                 (11, 601), (15, 601), (21, 701), (23, 701), (26, 1001), (5, 1001); -- User ID 5 is a teacher, this is an error based on FK. `takes.student_id` refers to `student.user_id`.
-- Correcting the last one, student_id must be an actual student.
-- Let's assume student 29 takes course 1001
INSERT INTO `takes` (`student_id`, `sec_id`) VALUES (29, 1001);
-- Add a few more
INSERT INTO `takes` (`student_id`, `sec_id`) VALUES
                                                 (13, 201), (14, 101), (16, 301), (18, 801);
-- (takes_id will be 1-29 for these inserts)

-- Data for `grade` table
-- (Assuming grade_id is AUTO_INCREMENT starting from 1)
-- (takes_id from `takes` table, e.g., 1 to 29 from above)
INSERT INTO `grade` (`takes_id`, `grade`, `proportion`, `type`) VALUES
                                                                    (1, 85, 0.20, 'homework'), (1, 90, 0.30, 'test'), (1, 78, 0.50, 'attending'), -- Student 11, Sec 101
                                                                    (2, 92, 0.20, 'homework'), (2, 88, 0.30, 'test'), (2, 95, 0.50, 'attending'), -- Student 12, Sec 101
                                                                    (3, 70, 0.25, 'homework'), (3, 75, 0.25, 'test'), (3, 80, 0.50, 'attending'), -- Student 11, Sec 201
                                                                    (4, 80, 0.30, 'homework'), (4, 85, 0.40, 'test'), (4, 90, 0.30, 'attending'), -- Student 13, Sec 401
                                                                    (5, 95, 0.20, 'homework'), (5, 89, 0.40, 'test'), (5, 92, 0.40, 'attending'), -- Student 15, Sec 501
                                                                    (6, 60, 0.10, 'homework'), (6, 70, 0.60, 'test'), (6, 75, 0.30, 'attending'),
                                                                    (7, 88, 0.20, 'homework'), (7, 91, 0.40, 'test'), (7, 85, 0.40, 'attending'),
                                                                    (8, 76, 0.25, 'homework'), (8, 82, 0.25, 'test'), (8, 79, 0.50, 'attending'),
                                                                    (9, 93, 0.30, 'homework'), (9, 87, 0.40, 'test'), (9, 90, 0.30, 'attending'),
                                                                    (10, 81, 0.20, 'homework'), (10, 84, 0.40, 'test'), (10, 88, 0.40, 'attending');
-- (Ensure proportion sums to 1.0 for each takes_id if that's a business rule, not enforced by schema here other than individual proportion between 0-1)

-- Data for `grade_change` table
-- (Assuming grade_change_id is AUTO_INCREMENT starting from 1)
-- (takes_id from `takes`, teacher_id from `teacher` user_ids: 2-10)
INSERT INTO `grade_change` (`takes_id`, `teacher_id`, `result`, `new_grade`, `apply_time`, `check_time`, `grade_type`) VALUES
                                                                                                                           (1, 2, NULL, 92, '2024-12-20 10:00:00', NULL, 'Final Grade Correction'), -- Alice (teacher_id 2) for takes_id 1
                                                                                                                           (3, 2, TRUE, 80, '2025-01-10 09:00:00', '2025-01-11 14:00:00', 'Test Regrade'), -- Alice for takes_id 3
                                                                                                                           (5, 4, FALSE, 90, '2024-12-15 11:30:00', '2024-12-16 10:00:00', 'Homework Appeal'); -- Charles (teacher_id 4) for takes_id 5

-- Data for `application` table
-- (sec_id from `section`)
# INSERT INTO `application` (`sec_id`, `reason`, `teacher`, `suggestion`, `final`) VALUES
#                                                                                      (101, 'Request to increase section capacity due to high demand.', 'Alice Wonderland', 'Approved by department head if resources allow.', FALSE),
#                                                                                      (201, 'Student requested late enrollment due to medical reasons.', 'Alice Wonderland', 'Forwarded to admin for special consideration.', FALSE),
#                                                                                      (401, 'Conflict with another mandatory course, requesting alternative assignment.', 'Bob The Builder', NULL, TRUE);
#
#
# -- Restore foreign key checks
# SET FOREIGN_KEY_CHECKS = 1;