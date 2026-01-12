-- ==================================================================================
-- UTMS Database Schema
-- Based on SDD Section 6.1: Logical Database Schema (Information Viewpoint)
-- Authors: Team 3 (Egehan Vardar, Mehmet Fatih Gülmez)
-- ==================================================================================

-- 1. USERS TABLE
-- Stores all actors (Student, OIDB, Dean, YGK, Admin).
-- "user_type" acts as the discriminator column for RBAC strategies.
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL, -- Storing BCrypt hash (SEC-03)
    email VARCHAR(100) NOT NULL UNIQUE, -- Institutional email required
    role VARCHAR(20) NOT NULL, -- ROLE_STUDENT, ROLE_OIDB, ROLE_DEAN, ROLE_YGK, ROLE_ADMIN
    user_type VARCHAR(20) NOT NULL, -- Discriminator for inheritance mapping
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. STUDENT_PROFILES TABLE
-- Extension of the USERS table for Student-specific attributes.
-- ONE-TO-ONE relationship with USERS.
CREATE TABLE student_profiles (
    user_id BIGINT PRIMARY KEY,
    tckn VARCHAR(11) NOT NULL UNIQUE, -- Turkish Identity Number
    current_university VARCHAR(100) NOT NULL,
    current_program VARCHAR(100) NOT NULL,
    overall_gpa DECIMAL(3, 2) NOT NULL CHECK (overall_gpa >= 0.00 AND overall_gpa <= 4.00), -- Enforcing 4.0 Scale
    CONSTRAINT fk_student_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 3. FACULTIES TABLE
-- Lookup table for University structure.
CREATE TABLE faculties (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- 4. DEPARTMENTS TABLE
-- Specific departments belonging to faculties.
-- Contains "quota" configuration (PR-08).
CREATE TABLE departments (
    id SERIAL PRIMARY KEY,
    faculty_id INTEGER NOT NULL,
    name VARCHAR(100) NOT NULL,
    quota INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT fk_dept_faculty FOREIGN KEY (faculty_id) REFERENCES faculties(id)
);

-- 5. APPLICATIONS TABLE
-- The core entity tracking the transfer request.
-- Status workflow: NEW -> FORWARDED -> UNDER_REVIEW -> FINALIZED -> (APPROVED/REJECTED)
-- Also handles RETURNED -> RESUBMITTED loops.
CREATE TABLE applications (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    target_department_id INTEGER NOT NULL,
    yks_score DECIMAL(6, 3) NOT NULL, -- National Exam Score (e.g., 480.123)
    converted_gpa DECIMAL(3, 2) NOT NULL, -- Normalized GPA
    composite_score DECIMAL(6, 3), -- Calculated: (GPA * 0.5) + (YKS * 0.5) (PR-07)
    submission_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'NEW',
    return_reason TEXT, -- Populated if status is RETURNED (UC-STU-02)
    CONSTRAINT fk_app_student FOREIGN KEY (student_id) REFERENCES users(id),
    CONSTRAINT fk_app_dept FOREIGN KEY (target_department_id) REFERENCES departments(id)
);

-- 6. DOCUMENTS TABLE
-- Stores metadata for uploaded files (PDFs).
-- Actual files are stored on disk; DB holds paths (SDD 6.2).
CREATE TABLE documents (
    id BIGSERIAL PRIMARY KEY,
    application_id BIGINT NOT NULL,
    document_type VARCHAR(50) NOT NULL, -- TRANSCRIPT, YKS_RESULT, ENGLISH_PROOF
    file_path VARCHAR(255) NOT NULL,
    file_size BIGINT NOT NULL, -- In Bytes, max 5MB (PR-11)
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_doc_app FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE CASCADE
);

-- 7. EVALUATIONS TABLE
-- Records the YGK Member's decision logic.
-- Ensures auditability of the decision process (PR-20).
CREATE TABLE evaluations (
    id BIGSERIAL PRIMARY KEY,
    application_id BIGINT NOT NULL,
    ygk_member_id BIGINT NOT NULL,
    is_eligible BOOLEAN NOT NULL DEFAULT FALSE,
    decision_note TEXT,
    evaluated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_eval_app FOREIGN KEY (application_id) REFERENCES applications(id),
    CONSTRAINT fk_eval_ygk FOREIGN KEY (ygk_member_id) REFERENCES users(id)
);