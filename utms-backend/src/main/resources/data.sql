-- ==================================================================================
-- UTMS Initial Seed Data
-- Pre-populating Faculty and Department data for testing.
-- ==================================================================================

-- Insert Faculties (Only those with active Dean's Office)
INSERT INTO faculties (name) VALUES ('Faculty of Engineering');
INSERT INTO faculties (name) VALUES ('Faculty of Architecture');

-- Insert Departments (Only those with active YGK)
-- Engineering (Faculty ID 1)
INSERT INTO departments (faculty_id, name, quota) VALUES (1, 'Computer Engineering', 5);
INSERT INTO departments (faculty_id, name, quota) VALUES (1, 'Mechanical Engineering', 3);

-- Architecture (Faculty ID 2)
INSERT INTO departments (faculty_id, name, quota) VALUES (2, 'Architecture', 5);

-- ==================================================================================
-- USERS (Password for all is 'password123')
-- Hash: $2a$10$3ZBvi9n99wC23zD88oJr6eZxG6M3mkn6cdLgmdWgWWGkRFkaG1yPa
-- ==================================================================================

-- 1. ADMIN
INSERT INTO users (username, password_hash, email, role, user_type, enabled) 
VALUES ('admin', '$2a$10$3ZBvi9n99wC23zD88oJr6eZxG6M3mkn6cdLgmdWgWWGkRFkaG1yPa', 'admin@iztech.edu.tr', 'ROLE_ADMIN', 'ADMIN', true);

-- 2. STUDENT
INSERT INTO users (username, password_hash, email, role, user_type, enabled) 
VALUES ('student', '$2a$10$3ZBvi9n99wC23zD88oJr6eZxG6M3mkn6cdLgmdWgWWGkRFkaG1yPa', 'student@iztech.edu.tr', 'ROLE_STUDENT', 'STUDENT', true);

-- Student Profile (Linked to User ID 2 - assuming auto-increment starts at 1, so admin=1, student=2)
-- Note: H2 usually resets IDs on restart in memory, but let's assume sequential insertion.
-- Using Select to be safe if possible, but H2 SQL support for subqueries in INSERT VALUES varies.
-- Hardcoding ID 2 is risky if IDs skip. 
-- However, for seed data on fresh DB, it usually works. 
/*
INSERT INTO student_profiles (user_id, tckn, current_university, current_program, overall_gpa)
VALUES (
    (SELECT id FROM users WHERE username = 'student'), 
    '12345678901', 
    'Ege University', 
    'Computer Science', 
    3.50
);
*/

-- 3. OIDB OFFICER
INSERT INTO users (username, password_hash, email, role, user_type, enabled) 
VALUES ('oidb', '$2a$10$3ZBvi9n99wC23zD88oJr6eZxG6M3mkn6cdLgmdWgWWGkRFkaG1yPa', 'oidb@iztech.edu.tr', 'ROLE_OIDB', 'STAFF', true);

-- 4. YGK MEMBER - Computer Engineering
INSERT INTO users (username, password_hash, email, role, user_type, enabled) 
VALUES ('ygk_cse', '$2a$10$3ZBvi9n99wC23zD88oJr6eZxG6M3mkn6cdLgmdWgWWGkRFkaG1yPa', 'ygk_cse@iztech.edu.tr', 'ROLE_YGK', 'ACADEMIC', true);

-- 5. YGK MEMBER - Mechanical Engineering
INSERT INTO users (username, password_hash, email, role, user_type, enabled) 
VALUES ('ygk_mech', '$2a$10$3ZBvi9n99wC23zD88oJr6eZxG6M3mkn6cdLgmdWgWWGkRFkaG1yPa', 'ygk_mech@iztech.edu.tr', 'ROLE_YGK', 'ACADEMIC', true);

-- 6. YGK MEMBER - Architecture
INSERT INTO users (username, password_hash, email, role, user_type, enabled) 
VALUES ('ygk_arch', '$2a$10$3ZBvi9n99wC23zD88oJr6eZxG6M3mkn6cdLgmdWgWWGkRFkaG1yPa', 'ygk_arch@iztech.edu.tr', 'ROLE_YGK', 'ACADEMIC', true);

-- 7. DEAN OF ENGINEERING FACULTY (Faculty Scope)
INSERT INTO users (username, password_hash, email, role, user_type, enabled)
VALUES ('dean_eng', '$2a$10$3ZBvi9n99wC23zD88oJr6eZxG6M3mkn6cdLgmdWgWWGkRFkaG1yPa', 'dean_eng@iztech.edu.tr', 'ROLE_DEAN_OFFICE_STAFF', 'ACADEMIC', true);

-- 8. DEAN OF ARCHITECTURE FACULTY
INSERT INTO users (username, password_hash, email, role, user_type, enabled)
VALUES ('dean_arch', '$2a$10$3ZBvi9n99wC23zD88oJr6eZxG6M3mkn6cdLgmdWgWWGkRFkaG1yPa', 'dean_arch@iztech.edu.tr', 'ROLE_DEAN_OFFICE_STAFF', 'ACADEMIC', true);

-- ADMINISTRATIVE PROFILES

-- Link 'dean_eng' to Faculty 1 (Engineering)
INSERT INTO administrative_profiles (user_id, department_id, faculty_id) VALUES (
    (SELECT id FROM users WHERE username = 'dean_eng'), NULL, 1
);

-- Link 'dean_arch' to Faculty 2 (Architecture)
INSERT INTO administrative_profiles (user_id, department_id, faculty_id) VALUES (
    (SELECT id FROM users WHERE username = 'dean_arch'), NULL, 2
);

-- Link 'ygk_cse' to Department 1 (Computer Engineering)
INSERT INTO administrative_profiles (user_id, department_id, faculty_id) VALUES (
    (SELECT id FROM users WHERE username = 'ygk_cse'), 1, NULL
);

-- Link 'ygk_mech' to Department 2 (Mechanical Engineering)
INSERT INTO administrative_profiles (user_id, department_id, faculty_id) VALUES (
    (SELECT id FROM users WHERE username = 'ygk_mech'), 2, NULL
);

-- Link 'ygk_arch' to Department 3 (Architecture)
INSERT INTO administrative_profiles (user_id, department_id, faculty_id) VALUES (
    (SELECT id FROM users WHERE username = 'ygk_arch'), 3, NULL
);