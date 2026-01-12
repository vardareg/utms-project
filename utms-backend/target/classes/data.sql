-- ==================================================================================
-- UTMS Initial Seed Data
-- Pre-populating Faculty and Department data for testing.
-- ==================================================================================

-- Insert Faculties
INSERT INTO faculties (name) VALUES ('Faculty of Engineering');
INSERT INTO faculties (name) VALUES ('Faculty of Science');
INSERT INTO faculties (name) VALUES ('Faculty of Architecture');

-- Insert Departments (with Quotas per PR-08)
-- Engineering
INSERT INTO departments (faculty_id, name, quota) VALUES (1, 'Computer Engineering', 5);
INSERT INTO departments (faculty_id, name, quota) VALUES (1, 'Mechanical Engineering', 3);
INSERT INTO departments (faculty_id, name, quota) VALUES (1, 'Civil Engineering', 4);

-- Science
INSERT INTO departments (faculty_id, name, quota) VALUES (2, 'Physics', 2);
INSERT INTO departments (faculty_id, name, quota) VALUES (2, 'Chemistry', 2);

-- Architecture
INSERT INTO departments (faculty_id, name, quota) VALUES (3, 'Architecture', 5);
INSERT INTO departments (faculty_id, name, quota) VALUES (3, 'City and Regional Planning', 3);

-- ==================================================================================
-- USERS (Password for all is 'password123')
-- Hash: $2a$10$3ZBvi9n99wC23zD88oJr6eZxG6M3mkn6cdLgmdWgWWGkRFkaG1yPa
-- ==================================================================================

-- 1. ADMIN
INSERT INTO users (username, password_hash, email, role, user_type) 
VALUES ('admin', '$2a$10$3ZBvi9n99wC23zD88oJr6eZxG6M3mkn6cdLgmdWgWWGkRFkaG1yPa', 'admin@iyte.edu.tr', 'ROLE_ADMIN', 'ADMIN');

-- 2. STUDENT
INSERT INTO users (username, password_hash, email, role, user_type) 
VALUES ('student', '$2a$10$3ZBvi9n99wC23zD88oJr6eZxG6M3mkn6cdLgmdWgWWGkRFkaG1yPa', 'student@std.iyte.edu.tr', 'ROLE_STUDENT', 'STUDENT');

-- Student Profile (Linked to User ID 2 - assuming auto-increment starts at 1, so admin=1, student=2)
-- Note: H2 usually resets IDs on restart in memory, but let's assume sequential insertion.
-- Using Select to be safe if possible, but H2 SQL support for subqueries in INSERT VALUES varies.
-- Hardcoding ID 2 is risky if IDs skip. 
-- However, for seed data on fresh DB, it usually works. 
INSERT INTO student_profiles (user_id, tckn, current_university, current_program, overall_gpa)
VALUES (
    (SELECT id FROM users WHERE username = 'student'), 
    '12345678901', 
    'Ege University', 
    'Computer Science', 
    3.50
);

-- 3. OIDB OFFICER
INSERT INTO users (username, password_hash, email, role, user_type) 
VALUES ('oidb', '$2a$10$3ZBvi9n99wC23zD88oJr6eZxG6M3mkn6cdLgmdWgWWGkRFkaG1yPa', 'oidb@iyte.edu.tr', 'ROLE_OIDB', 'STAFF');

-- 4. YGK MEMBER (Transfer Commission)
INSERT INTO users (username, password_hash, email, role, user_type) 
VALUES ('ygk', '$2a$10$3ZBvi9n99wC23zD88oJr6eZxG6M3mkn6cdLgmdWgWWGkRFkaG1yPa', 'ygk@iyte.edu.tr', 'ROLE_YGK', 'ACADEMIC');

-- 5. DEAN
INSERT INTO users (username, password_hash, email, role, user_type) 
VALUES ('dean', '$2a$10$3ZBvi9n99wC23zD88oJr6eZxG6M3mkn6cdLgmdWgWWGkRFkaG1yPa', 'dean@iyte.edu.tr', 'ROLE_DEAN', 'ACADEMIC');