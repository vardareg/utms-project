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

-- Insert a System Administrator (Bootstrap User)
-- Password is 'admin123' (BCrypt hash)
INSERT INTO users (username, password_hash, email, role, user_type) 
VALUES ('admin', '$2a$10$Dow.w/y0y.h.u.g.s.t.h.e.r.e.s.a.l.t.h.a.s.h.v.a.l.u.e', 'admin@iyte.edu.tr', 'ROLE_ADMIN', 'ADMIN');