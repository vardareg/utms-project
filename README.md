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
- PostgreSQL (Optional; creating `application.properties` allows switching between H2 In-Memory and Postgres).
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
*Note: The current repository state has all necessary code pre-populated.*

**Backend Configuration**:
The system uses an **H2 In-Memory Database** by default for ease of development. Configuration is found in `src/main/resources/application.properties`.

**Frontend Configuration**:
The frontend is a **Vite + React** app styled with **Tailwind CSS**.

## 5. Running the Application

### Backend (Spring Boot)

1. Navigate to the backend directory:

   ```bash
   cd utms-project/utms-backend
   ```

2. Run the application using Maven:

   ```bash
   mvn clean spring-boot:run
   ```

   Server will start on `http://localhost:8080`.

### Frontend (React)

1. Navigate to the frontend directory:

   ```bash
   cd utms-project/utms-frontend
   ```

2. Install dependencies (including Tailwind CSS):

   ```bash
   npm install
   ```

3. Start the development server:

   ```bash
   npm run dev
   ```

   Client will start on `http://localhost:5173`.

## 6. Quick Start Credentials

See `credentials.txt` for a full list. All accounts use **Password:** `password123`.

| Role | Username |
| :--- | :--- |
| **Student** | `student` |
| **Student Affairs (ÖİDB)** | `oidb` |
| **Transfer Commission (YGK)** | `ygk` |
| **Admin** | `admin` |

## 7. Usage Guide (Flows)

The system is pre-seeded with data (via `data.sql`).

### 1. Student Flow (WP-3)

- **Login**: Use `student`.
- **Action**: Click "New Application", select Department, enter YKS Score, Upload PDF.
- **Outcome**: Status becomes `NEW`.

### 2. ÖİDB Officer Flow (WP-4)

- **Login**: Use `oidb`.
- **Action**: View "Pending Evaluations", Download Documents to verify.
- **Outcome**: Click "Forward" -> Status becomes `FORWARDED`.

### 3. YGK Member Flow (WP-5)

- **Login**: Use `ygk`.
- **Action**: View "Evaluations", click "Evaluate", mark "Eligible".
- **Outcome**: Status becomes `UNDER_REVIEW`.
- **Ranking**: Click "Ranking List" to see the auto-generated Primary/Waitlist.

## 8. API Documentation

For detailed API endpoints, request/response formats, and business rules logic, refer to the `manual.md` file (formerly `API_DOCUMENTATION.md`) included in the delivery package.

## 9. Troubleshooting

- **Login Fails (403/401)**: Ensure the backend is running. If recently restarted, the in-memory H2 database resets; ensure you are using the seeded credentials.
- **Database Errors**: The system uses `application.properties` to configure H2. Ensure `schema.sql` (if present) does not conflict with Hibernate's `ddl-auto` setting. Currently, `schema.sql.bak` is disabled to allow Hibernate to manage the schema.
- **CORS Issues**: The `SecurityConfig.java` is configured to allow requests from `localhost:5173`.
- **Styling Missing**: Ensure you ran `npm install` to download Tailwind dependencies and `npm run dev` to compile the styles.

---
© 2026 IZTECH Team 3
