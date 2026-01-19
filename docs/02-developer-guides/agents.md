# Agent Operational Guide

This repository contains the Undergraduate Transfer Management System (UTMS), organized as a multi-project repository.

- **Backend (`utms-backend`):** Spring Boot 3.2 + Java 17 (Layered Architecture).
- **Frontend (`utms-frontend`):** React 18 + Vite + Tailwind CSS.
- **Database:** H2 (Dev) or PostgreSQL (Prod).

## 1. Quick Start (Preferred)

The project includes helper scripts in the `scripts/` directory.

- **Start Everything:** `bash scripts/start-utms.sh` (Runs Docker containers + Frontend)
- **Start Backend (Docker):** `bash scripts/start-backend.sh`
- **Start Frontend:** `bash scripts/start-frontend.sh`
- **Seed Database:** `bash scripts/seed-database.sh`
- **Stop Everything:** `bash scripts/stop-utms.sh`

## 2. Manual Build & Run Commands

### Backend (`utms-backend`)
Run from `utms-backend/`.

- **Build:** `mvn clean install`
- **Run (Dev Profile - H2):** `mvn spring-boot:run`
- **Run (Prod Profile - Postgres):** `mvn spring-boot:run -Dspring.profiles.active=prod`
- **Run All Tests:** `mvn test`
- **Run Single Test:** `mvn test -Dtest=ClassName#methodName`
- **Dependency Tree:** `mvn dependency:tree`

### Frontend (`utms-frontend`)
Run from `utms-frontend/`.

- **Install:** `npm install`
- **Run Dev:** `npm run dev` (http://localhost:5173)
- **Build:** `npm run build`
- **Lint:** `npm run lint`

## 3. Test Data & Credentials

### Generating Data
Use the Python script to generate 50 test students and applications:
```bash
python3 tools/init_data.py
```

### Default Credentials
All default passwords are `Password123!` (Test students: `Student123!`).

| Role | Username | Description |
|------|----------|-------------|
| **Admin** | `admin` | System management |
| **OIDB** | `oidb` | Student Affairs (Docs) |
| **Dean** | `dean_eng` | Engineering Faculty |
| **YGK** | `ygk_cse` | Computer Engineering |
| **Student** | `student` | Generic student |

## 4. Code Style & Architecture

### Backend (Java/Spring Boot)
- **Injection:** STRICTLY use Constructor Injection (`@RequiredArgsConstructor`). No `@Autowired` on fields.
- **Lombok:** Use `@Data`, `@Builder`, `@RequiredArgsConstructor`.
- **DTOs:** Always map Entities to DTOs before returning from Controllers.
- **Security:** Spring Security + JWT. `SecurityContextHolder` holds current user.
- **Validation:** `jakarta.validation` annotations on DTOs.

### Frontend (React/Vite)
- **Styling:** Tailwind CSS (`red-900`, `gray-50`).
- **Icons:** `lucide-react`.
- **API:** Use `apiFetch` from `services/api.js` (handles Auth headers).
- **Routing:** `react-router-dom` v6.

## 5. Workflow for Agents

1. **Check Profiles:** deciding between `dev` (H2, resets on restart) and `prod` (Postgres, persistent).
2. **Implement:** Follow standard Layered Architecture (Controller -> Service -> Repo).
3. **Verify:**
   - Backend: `mvn test`
   - Frontend: `npm run lint` && `npm run build`
4. **Troubleshoot:**
   - **Port 8080 used:** `fuser -k 8080/tcp`
   - **DB Connection:** Check `docker compose logs db` or ensure H2 console is enabled in dev.
   - **Auth:** If 401/403, check `docs/test-credentials.txt` or re-login (tokens expire in 30m).

## 6. Important Paths
- **Scripts:** `/home/egehan/Documents/Projects/IYTE/utms-project/scripts`
- **Backend:** `/home/egehan/Documents/Projects/IYTE/utms-project/utms-backend`
- **Frontend:** `/home/egehan/Documents/Projects/IYTE/utms-project/utms-frontend`
- **Tools:** `/home/egehan/Documents/Projects/IYTE/utms-project/tools`
