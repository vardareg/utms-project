# Undergraduate Transfer Management System (UTMS)

**Team 3 - IZTECH Computer Engineering**
**Course:** SEDS 505
**Version:** 1.0.0 (MVP)

## 1. Project Overview

The **Undergraduate Transfer Management System (UTMS)** is a web-based platform designed to automate the undergraduate transfer (yatay geçiş) workflow at IZTECH. It replaces manual processes with a secure, centralized digital system connecting Students, Student Affairs (ÖİDB), Deans, and Transfer Commissions (YGK).

### Key Capabilities
- **Students**: Submit applications, upload documents, track status.
- **ÖİDB**: Validate documents, forward to faculties, return incomplete applications.
- **YGK**: Evaluate academic eligibility, view auto-generated rankings based on Composite Scores.

## 2. Architecture

The system follows a 3-Layer Monolithic Architecture as defined in the Software Design Description (SDD):

- **Frontend**: React.js (Single Page Application) with Tailwind CSS.
- **Backend**: Spring Boot 3.2 (Java 17) with RESTful APIs.
- **Database**: PostgreSQL (Relational Schema in 3NF).
- **Security**: JWT (JSON Web Tokens) for Stateless Authentication + RBAC.

## 3. Prerequisites

Ensure you have the following installed:
- Java Development Kit (JDK) 17 or higher.
- Node.js (v18+) and npm.
- Maven (v3.8+).
- PostgreSQL (Optional for local dev; the current configuration uses H2 In-Memory or expects a local Postgres instance).
- Bash Terminal (Linux/Mac) or Git Bash (Windows).

## 4. Installation & Setup

### Step 1: Initialize Workspace
We provide an automated script to create the standard directory structure.
1. Save the `setup_utms_structure.sh` script to your desired location.
2. Run the script:
   ```bash
   chmod +x setup_utms_structure.sh
   ./setup_utms_structure.sh
   ```
   This creates the `utms-project` folder containing `utms-backend` and `utms-frontend`.

### Step 2: Populate Code
The script creates empty files. You must populate them with the code generated in the development phases (WP-2 to WP-5).

**Backend Mappings** (`utms-backend/src/main/java/com/iztech/utms/...`):
- **Model**: `User.java`, `Application.java`, `Document.java`, `Evaluation.java`, `StudentProfile.java`, `UniversityStructure.java`.
- **Repository**: `CoreRepositories.java`, `ApplicationRepository.java`, `EvaluationRepository.java`, etc.
- **Service**: `ApplicationService.java`, `FileStorageService.java`, `EvaluationService.java`.
- **Controller**: `AuthController.java`, `ApplicationController.java`, `DocumentController.java`, `EvaluationController.java`.
- **Security**: `JwtUtils.java`, `SecurityConfig.java`, `AuthTokenFilter.java`, `UserDetailsServiceImpl.java`.

**Resources** (`utms-backend/src/main/resources/`):
- `schema.sql` (Database DDL)
- `data.sql` (Seed Data)

**Frontend** (`utms-frontend/src/`):
- `App.jsx` (Contains the full UI logic: Login, Student Dashboard, ÖİDB Dashboard, YGK Dashboard).

## 5. Running the Application

### Backend (Spring Boot)
1. Navigate to the backend directory:
   ```bash
   cd utms-project/utms-backend
   ```
2. Run the application using Maven:
   ```bash
   mvn spring-boot:run
   ```
   Server will start on `http://localhost:8080`.

### Frontend (React)
1. Navigate to the frontend directory:
   ```bash
   cd utms-project/utms-frontend
   ```
2. Install dependencies:
   ```bash
   npm install react react-dom lucide-react
   ```
3. Start the development server:
   ```bash
   npm run dev
   ```
   Client will start on `http://localhost:5173` (or similar).

## 6. Usage Guide (Roles & Credentials)

The system is pre-seeded with data (via `data.sql`). You can use the following flows to test the Work Packages:

### 1. Student Flow (WP-3)
- **Login**: Register or use a seeded student user (if configured).
- **Action**: Click "New Application", select Department, enter YKS Score, Upload PDF.
- **Outcome**: Status becomes `NEW`.

### 2. ÖİDB Officer Flow (WP-4)
- **Login**: Use an account with `ROLE_OIDB`.
- **Action**: View "Pending Evaluations", Download Documents to verify.
- **Outcome**: Click "Forward" -> Status becomes `FORWARDED`.

### 3. YGK Member Flow (WP-5)
- **Login**: Use an account with `ROLE_YGK`.
- **Action**: View "Evaluations", click "Evaluate", mark "Eligible".
- **Outcome**: Status becomes `UNDER_REVIEW`.
- **Ranking**: Click "Ranking List" to see the auto-generated Primary/Waitlist.

## 7. API Documentation

For detailed API endpoints, request/response formats, and business rules logic, refer to the `manual.md` file (formerly `API_DOCUMENTATION.md`) included in the delivery package.

## 8. Troubleshooting

- **Database Errors**: Ensure `schema.sql` matches the Entity classes exactly. If using H2, the console is at `/h2-console`.
- **CORS Issues**: If Frontend cannot talk to Backend, ensure `@CrossOrigin` is present on Controllers and SecurityConfig permits the frontend origin.
- **File Upload**: Ensure the `uploads` directory exists in the backend root (the script creates this automatically).

---
© 2026 IZTECH Team 3