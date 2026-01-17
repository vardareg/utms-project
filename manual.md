# UTMS Technical API Documentation

**Version:** 1.1.0  
**Generated:** January 2026  
**Team:** 3 (Izmir Institute of Technology)

## 1. Overview

The Undergraduate Transfer Management System (UTMS) is a monolithic Spring Boot application designed to automate the transfer workflow. This document details the implementation of the Business Logic Layer and REST API endpoints, mapping them to the Software Requirements Specification (SRS).

## 2. Package: com.iztech.utms.controller (Presentation Layer)

Handles HTTP requests, JSON serialization, and Role-Based Access Control (RBAC).

### AuthController

**Base URL:** `/api/auth`  
**Description:** Manages user entry and JWT generation.

**Methods:**

- `POST /login`
  - **Implements:** UC-SYS-01 (User Login)
  - **Security:** SEC-03 (BCrypt Password Validation)
  - **Returns:** JWT Token + User Role + User Details (Name, Email).

- `POST /register`
  - **Role:** PUBLIC
  - **Implements:** Student Self-Registration
  - **Payload:** Username, Email, Password (SEC-09), TCKN, First Name, Last Name
  - **Validations:** Unique Index checks (Username, Email, TCKN)
  - **Logic:** Creates `ROLE_STUDENT` user and initial `StudentProfile` (only TCKN).

### ApplicationController

**Base URL:** `/api/applications`  
**Description:** Core workflow controller for Students, OIDB, and Dean's Office Staff.

**Methods:**

- `POST /`
  - **Role:** STUDENT
  - **Implements:** UC-STU-01 (Submit Application)
  - **Logic:** Triggers strict validation via ApplicationService.
  - **Validations:** PR-01 (GPA ≥ 2.50), PR-15 (No duplicates)

- `GET /my-application`
  - **Role:** STUDENT
  - **Returns:** Student's own application details

- `GET /status/{status}`
  - **Role:** OIDB, DEAN_OFFICE_STAFF, YGK
  - **Implements:** UC-OIDB-01 (View Incoming Queue)

- `PATCH /{id}/forward`
  - **Role:** OIDB
  - **Logic:** Transitions status `NEW` → `UNDER_REVIEW`.
  - **Automation:** Automatically assigns application to YGK for evaluation (Bypasses manual Dean assignment).

- `PATCH /{id}/return`
  - **Role:** OIDB
  - **Logic:** Transitions status `NEW` → `RETURNED`.
  - **Requires:** Return reason in request body

- `PATCH /{id}/approve`
  - **Role:** DEAN_OFFICE_STAFF
  - **Logic:** Final approval after YGK evaluation
  - **Transitions:** `FINALIZED` → `APPROVED`

- `PATCH /return-to-ygk/{departmentId}`
  - **Role:** DEAN_OFFICE_STAFF
  - **Logic:** Return a finalized ranking list to YGK for revision.
  - **Transitions:** `FINALIZED` → `UNDER_REVIEW` (Bulk)
  - **Requires:** Revision reason in request body

### StudentController

**Base URL:** `/api/student/profile`  
**Description:** Student profile management.

**Methods:**

- `GET /`
  - **Role:** STUDENT
  - **Returns:** Current user's profile

- `POST /`
  - **Role:** STUDENT
  - **Creates/Updates:** Student profile (University, Program, GPA)
  - **Flow:** Used during mandatory "Profile Completion" phase after first login.
  - **Validations:** TCKN format, GPA range (0.00-4.00)

### DocumentController

**Base URL:** `/api/documents`  
**Description:** Handles file I/O operations.

**Methods:**

- `POST /upload`
  - **Role:** STUDENT
  - **Implements:** PR-11 (File Upload Constraints)
  - **Constraint:** Only accepts PDF, max 5MB.

- `GET /download/{id}`
  - **Role:** OIDB, DEAN_OFFICE_STAFF, YGK, STUDENT (Owner)
  - **Implements:** UC-OIDB-01 (Document Verification).

### EvaluationController

**Base URL:** `/api/evaluations`  
**Description:** Managing the scoring and ranking engine.

**Methods:**

- `POST /{appId}`
  - **Role:** YGK
  - **Implements:** UC-YGK-01 (Evaluate Candidate)
  - **Payload:** `isEligible` (boolean), `note` (string)
  - **Logic:** Saves "Draft" evaluation decision. Does **NOT** change application status.

- `POST /finalize/{deptId}`
  - **Role:** YGK
  - **Logic:** Finalizes all draft evaluations for the department.
  - **Transitions:** `UNDER_REVIEW` → `FINALIZED` (Eligible) or `REJECTED` (Ineligible).

- `GET /ranking/{deptId}`
  - **Role:** YGK, DEAN_OFFICE_STAFF
  - **Implements:** PR-10 (Ranking Generation)
  - **Logic:** Returns `RankingResponse` containing Primary and Waitlist.

- `GET /ranking/{deptId}/export`
  - **Role:** YGK, DEAN_OFFICE_STAFF, OIDB, ADMIN
  - **Exports:** Ranking list as PDF or Excel
  - **Query Param:** `format` (pdf/excel)

### UniversityStructureController

**Base URL:** `/api/structure`  
**Description:** Provides faculty and department data for UI dropdowns.

**Methods:**

- `GET /faculties`
  - **Role:** ADMIN
  - **Returns:** List of all faculties with departments

- `GET /departments`
  - **Role:** ADMIN
  - **Returns:** List of all departments with faculty relationships

### AdminController

**Base URL:** `/api/admin`  
**Description:** User management for administrators.

**Methods:**

- `GET /users`
  - **Role:** ADMIN
  - **Returns:** All users with their roles and assignments

- `POST /users`
  - **Role:** ADMIN
  - **Creates:** New user with specified role
  - **Optional:** `facultyId` or `departmentId` for DEAN_OFFICE_STAFF/YGK roles

- `PUT /users/{id}`
  - **Role:** ADMIN
  - **Updates:** User details, role, or assignments
  - **Logic:** Auto-creates/updates/deletes `AdministrativeProfile` based on role

## 3. Package: com.iztech.utms.service (Business Logic Layer)

Contains the transactional logic and business rules.

### ApplicationService

**Dependencies:** `StudentProfileRepository`, `DepartmentRepository`, `OsymService`, `ScoringService`

**Key Methods:**

- `submitApplication(username, request)`
  - **PR-01:** Checks if GPA ≥ 2.50.
  - **PR-15:** Checks for duplicate applications in the same department.
  - **PR-07:** Calculates Composite Score = (Converted GPA × 0.5) + (YKS × 0.5).
  - **External Validation:** Validates YKS score with OSYM service (soft validation)

- `getApplicationsByStatus(status)`
  - **Logic:** Returns filtered DTOs for dashboard views.

- `forwardApplication(id)`
  - **OIDB Action:** Forwards application to faculty/department
  - **Automated:** Sets status to `UNDER_REVIEW`.

- `approveApplication(id)`
  - **Dean Action:** Final approval after YGK evaluation

- `returnToYgk(departmentId, reason)`
  - **Dean Action:** Returns all `FINALIZED` applications of a department to `UNDER_REVIEW`.
  - **Logic:** Prefixes `returnReason` with `[DEAN RETURN]` for YGK visibility and logs audit events for each application.

### UserService

**Dependencies:** `UserRepository`, `AdministrativeProfileRepository`, `FacultyRepository`, `DepartmentRepository`

**Key Methods:**

- `createUser(request)`
  - **Validates:** Password complexity (SEC-09)
  - **Creates:** User with BCrypt hashed password
  - **Auto-creates:** `AdministrativeProfile` for DEAN_OFFICE_STAFF/YGK roles

- `updateUser(id, request)`
  - **Updates:** User details and role
  - **Auto-manages:** Administrative profile based on role changes
  - **Clears assignments:** When role changes to non-administrative type

### FileStorageService

**Dependencies:** Java NIO (New I/O)

**Key Methods:**

- `storeDocument(username, appId, type, file)`
  - **Security:** Verifies that the uploading user owns the application.
  - **Naming:** Renames file to `APP_{ID}_{TYPE}_{TIMESTAMP}.pdf` to prevent collisions.
  - **Validation:** Ensures PDF format and size ≤ 5MB

- `loadFileAsResource(docId, username)`
  - **Logic:** Loads file from disk for download.

### EvaluationService

**Dependencies:** `EvaluationRepository`, `ApplicationRepository`

**Key Methods:**

- `submitEvaluation(username, appId, isEligible, note)`
  - **Logic:** Records YGK member's draft decision. Does not change application status.
  - **Scope Validation:** Ensures YGK member can only evaluate their department's applications

- `finalizeEvaluations(deptId)`
  - **Logic:** Applies draft decisions to applications. Transitions eligible applications to `FINALIZED`.

- `generateRanking(deptId)`
  - **PR-10:** Fetches all evaluated apps, sorts by Composite Score (Descending).
  - **PR-08:** Splits list into Primary and Waitlist based on `Department.quota`.

## 4. Package: com.iztech.utms.model (Domain Entities)

Mappings to H2/PostgreSQL Database Schema (SDD 6.1).

- **User**: Base entity for RBAC. Contains `passwordHash` (SEC-03), `role`, `userType`.
  - Roles: `ROLE_ADMIN`, `ROLE_STUDENT`, `ROLE_OIDB`, `ROLE_YGK`, `ROLE_DEAN_OFFICE_STAFF`
  
- **StudentProfile**: Extends User. Contains `tckn`, `currentUniversity`, `currentProgram`, `overallGpa`, `hasDisciplinaryRecord`.

- **AdministrativeProfile**: Links users to faculties or departments.
  - Links DEAN_OFFICE_STAFF to faculties
  - Links YGK members to departments

- **Application**: Core entity.
  - `status`: Enum (NEW, FORWARDED, RETURNED, RESUBMITTED, UNDER_REVIEW, EVALUATED, APPROVED, REJECTED)
  - `compositeScore`: Stored value for performance (PR-07).
  - `yksScore`: Student's YKS entrance exam score
  - `convertedGpa`: GPA converted to 100-scale

- **Document**: Metadata for uploaded files.
  - Supports types: TRANSCRIPT, ID_CARD, PETITION

- **UniversityStructure**: Contains Faculty and Department configuration (PR-08 Quota).
  - Currently supports: Faculty of Engineering, Faculty of Architecture
  - Departments: Computer Engineering, Mechanical Engineering, Architecture

## 5. Security Configuration

**Class:** `SecurityConfig.java`  
**Standard:** Spring Security 6 / OAuth2 Resource Server patterns.

**Mechanism:**

- **Stateless:** No server-side sessions (`SessionCreationPolicy.STATELESS`).
- **JWT:** Bearer token authentication via `AuthTokenFilter`.
  - **Expiration:** 30 minutes (SEC-06)
  - **Algorithm:** HS256
- **Password:** BCrypt hashing with strength factor 10 (SEC-03)
- **Password Policy:** Minimum 8 chars, must include uppercase, lowercase, numbers, special characters (SEC-09)
- **CORS:** Configured to allow requests from Frontend origin (`http://localhost:5173`).
- **Public Endpoints:** `/api/auth/**`
- **Authenticated Endpoints:** All others require valid JWT token

## 6. Role-Based Access Control (RBAC)

| Role | Access Level | Permissions |
|------|-------------|-------------|
| **ROLE_ADMIN** | System-wide | User management, view all data |
| **ROLE_STUDENT** | Self-only | Submit/edit application, upload documents |
| **ROLE_OIDB** | All applications | Validate, forward, return applications |
| **ROLE_YGK** | Department-scoped | Evaluate applications, generate rankings |
| **ROLE_DEAN_OFFICE_STAFF** | Faculty/Department-scoped | Assign to YGK, approve/reject applications |

## 7. Data Seeding

**File:** `data.sql`  
**Purpose:** Pre-populate database with faculties, departments, and default users.

**Seeded Users:**

- `admin` (ROLE_ADMIN)
- `student` (ROLE_STUDENT)
- `oidb` (ROLE_OIDB)
- `ygk_cse` (ROLE_YGK - Computer Engineering)
- `ygk_mech` (ROLE_YGK - Mechanical Engineering)
- `ygk_arch` (ROLE_YGK - Architecture)
- `dean_eng` (ROLE_DEAN_OFFICE_STAFF - Engineering Faculty)
- `dean_arch` (ROLE_DEAN_OFFICE_STAFF - Architecture Faculty)

**Password:** `password123` for all seeded users

## 8. Testing Utilities

### init_data.py

**Purpose:** Generate 50 test students with realistic data for comprehensive testing.

**Features:**

- Creates students distributed across 3 departments
- Generates varied GPAs (2.5-4.0) and YKS scores (400-520)
- Simulates different application statuses
- Auto-creates profiles and submits applications
- Can simulate full workflow (OIDB → YGK → Dean)

**Usage:**

```bash
python3 init_data.py
```

**Test Student Password:** `Student123!`

---

© 2026 IZTECH Team 3
