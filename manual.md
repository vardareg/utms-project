# UTMS Technical API Documentation

**Version:** 1.0.0
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
  - **Returns:** JWT Token + User Role.

### ApplicationController
**Base URL:** `/api/applications`
**Description:** Core workflow controller for Students and OIDB.

**Methods:**
- `POST /`
  - **Role:** STUDENT
  - **Implements:** UC-STU-01 (Submit Application)
  - **Logic:** Triggers strict validation via ApplicationService.

- `GET /status/{status}`
  - **Role:** OIDB, DEAN, YGK
  - **Implements:** UC-OIDB-01 (View Incoming Queue)

- `PATCH /{id}/forward`
  - **Role:** OIDB
  - **Logic:** Transitions status `NEW` -> `FORWARDED`.

- `PATCH /{id}/return`
  - **Role:** OIDB
  - **Logic:** Transitions status `NEW` -> `RETURNED`.

### DocumentController
**Base URL:** `/api/documents`
**Description:** Handles file I/O operations.

**Methods:**
- `POST /upload`
  - **Role:** STUDENT
  - **Implements:** PR-11 (File Upload Constraints)
  - **Constraint:** Only accepts PDF, max 5MB.

- `GET /download/{id}`
  - **Role:** OIDB, DEAN, YGK, STUDENT (Owner)
  - **Implements:** UC-OIDB-01 (Document Verification).

### EvaluationController
**Base URL:** `/api/evaluations`
**Description:** Managing the scoring and ranking engine.

**Methods:**
- `POST /{appId}`
  - **Role:** YGK
  - **Implements:** UC-YGK-01 (Evaluate Candidate)

- `GET /ranking/{deptId}`
  - **Role:** YGK, DEAN
  - **Implements:** PR-10 (Ranking Generation)
  - **Logic:** Returns `RankingResponse` containing Primary and Waitlist.

## 3. Package: com.iztech.utms.service (Business Logic Layer)

Contains the transactional logic and business rules.

### ApplicationService
**Dependencies:** `StudentProfileRepository`, `DepartmentRepository`

**Key Methods:**
- `submitApplication(username, request)`
  - **PR-01:** Checks if GPA >= 2.50.
  - **PR-15:** Checks for duplicate applications in the same department.
  - **PR-07:** Calculates Composite Score = (GPA * 0.5) + (YKS * 0.5).

- `getApplicationsByStatus(status)`
  - **Logic:** Returns filtered DTOs for dashboard views.

### FileStorageService
**Dependencies:** Java NIO (New I/O)

**Key Methods:**
- `storeDocument(username, appId, type, file)`
  - **Security:** Verifies that the uploading user owns the application.
  - **Naming:** Renames file to `APP_{ID}_{TYPE}_{TIMESTAMP}.pdf` to prevent collisions.

- `loadFileAsResource(docId, username)`
  - **Logic:** Loads file from disk for download.

### EvaluationService
**Dependencies:** `EvaluationRepository`

**Key Methods:**
- `submitEvaluation(username, appId, isEligible, note)`
  - **Logic:** Records YGK member's decision. If ineligible, auto-rejects application.

- `generateRanking(deptId)`
  - **PR-10:** Fetches all apps, sorts by Composite Score (Descending).
  - **PR-08:** Splits list into Primary and Waitlist based on `Department.quota`.

## 4. Package: com.iztech.utms.model (Domain Entities)

Mappings to PostgreSQL Database Schema (SDD 6.1).

- **User**: Base entity for RBAC. Contains `passwordHash` (SEC-03).
- **StudentProfile**: Extends User. Contains `overallGpa` (used for PR-01).
- **Application**: Core entity.
  - `status`: Enum (NEW, FORWARDED, APPROVED, etc.).
  - `compositeScore`: Stored value for performance (PR-07).
- **Document**: Metadata for uploaded files.
- **UniversityStructure**: Contains static Faculty and Department config (PR-08 Quota).

## 5. Security Configuration

**Class:** `SecurityConfig.java`
**Standard:** Spring Security 6 / OAuth2 Resource Server patterns.

**Mechanism:**
- **Stateless:** No server-side sessions (`SessionCreationPolicy.STATELESS`).
- **JWT:** Bearer token authentication via `AuthTokenFilter`.
- **Cors:** Configured to allow requests from Frontend origin.