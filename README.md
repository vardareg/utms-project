# Undergraduate Transfer Management System (UTMS)

**Team 3 - IZTECH Computer Engineering**  
**Course:** SEDS 505  
**Version:** 1.1.0

## 1. Project Overview

The **Undergraduate Transfer Management System (UTMS)** is a web-based platform designed to automate the undergraduate transfer (yatay geçiş) workflow at IZTECH. It replaces manual processes with a secure, centralized digital system connecting Students, Student Affairs (ÖİDB), Dean's Office Staff, and Transfer Commissions (YGK).

### Key Capabilities

- **Students**: Register accounts, complete profiles, submit applications, upload documents, track status in real-time.
- **ÖİDB**: Validate documents, forward to faculties, return incomplete applications.
- **YGK (Transfer Commission)**: Evaluate academic eligibility, view auto-generated rankings based on Composite Scores.
- **Dean's Office Staff**: Assign applications to YGK, make final approval/rejection decisions.
- **Admin**: Manage users, assign faculty/department scopes, view system-wide data.

## 2. Architecture

The system follows a 3-Layer Monolithic Architecture as defined in the Software Design Description (SDD):

- **Frontend**: React.js (Single Page Application) with Vite and Vanilla CSS.
- **Backend**: Spring Boot 3.2 (Java 17) with RESTful APIs.
- **Database**: H2 In-Memory (Development) / PostgreSQL (Production).
- **Security**: JWT (JSON Web Tokens) for Stateless Authentication + RBAC.

## 3. Prerequisites

Ensure you have the following installed:

- **Java Development Kit (JDK)** 17 or higher
- **Node.js** (v18+) and npm
- **Maven** (v3.8+)
- **Python 3** (for test data generation scripts)
- Bash Terminal (Linux/Mac) or Git Bash (Windows)

## 4. Installation & Setup

### Step 1: Clone Repository

```bash
git clone <repository-url>
cd utms-project
```

### Step 2: Backend Configuration

The system uses an **H2 In-Memory Database** by default for ease of development. Configuration is found in `utms-backend/src/main/resources/application.properties`.

**Default Configuration:**

- Database resets on each restart
- Pre-seeded with default users and university structure
- No external database setup required

### Step 3: Frontend Configuration

The frontend is a **Vite + React** app styled with Vanilla CSS.

- Dynamic faculty and department dropdowns
- Color-coded role badges
- Real-time status tracking

## 5. Running the Application

### Backend (Spring Boot)

1. Navigate to the backend directory:

   ```bash
   cd utms-backend
   ```

2. Run the application using Maven:

   ```bash
   mvn clean spring-boot:run
   ```

   Server will start on `http://localhost:8080`.

### Frontend (React)

1. Navigate to the frontend directory:

   ```bash
   cd utms-frontend
   ```

2. Install dependencies:

   ```bash
   npm install
   ```

3. Start the development server:

   ```bash
   npm run dev
   ```

   Client will start on `http://localhost:5173`.

## 6. Default Credentials

See `credentials.txt` for a complete list.

### Default Accounts (Password: `password123`)

| Role | Username | Scope |
|------|----------|-------|
| **Admin** | `admin` | System-wide |
| **Student** | `student` | Self only |
| **ÖİDB Officer** | `oidb` | All applications |
| **YGK - Computer Eng** | `ygk_cse` | Computer Engineering |
| **YGK - Mechanical Eng** | `ygk_mech` | Mechanical Engineering |
| **YGK - Architecture** | `ygk_arch` | Architecture |
| **Dean's Office - Engineering** | `dean_eng` | Engineering Faculty |
| **Dean's Office - Architecture** | `dean_arch` | Architecture Faculty |

### Test Students (Password: `Student123!`)

50 test students can be generated using the `init_data.py` script:

```bash
python3 init_data.py
```

Test students follow the naming pattern: `student[01-50]_[dept]_[status]`

- **Departments**: `cse`, `mech`, `arch`
- **Statuses**: `new`, `forwarded`, `evaluated`, `approved`, `rejected`

## 7. University Structure

### Active Faculties and Departments

**Faculty of Engineering (ID: 1)**

- Computer Engineering (ID: 1, Quota: 5)
- Mechanical Engineering (ID: 2, Quota: 3)

**Faculty of Architecture (ID: 2)**

- Architecture (ID: 3, Quota: 5)

## 8. Usage Guide (Flows)

### 1. Student Flow

1. **Registration**: Click "Create Student Account", enter TCKN, email, username, and password.
2. **Login**: Use credentials created during registration.
3. **Complete Profile**: Redirected to profile form to enter University, Program, and GPA.
4. **Submit Application**: Select target department, enter YKS score.
5. **Upload Documents**: Attach required PDFs (max 5MB each).
6. **Track Status**: Monitor application progress in real-time.

### 2. ÖİDB Officer Flow

1. **Login**: Use `oidb`
2. **View Applications**: See all incoming applications with status `NEW`
3. **Validate Documents**: Download and verify uploaded files
4. **Actions**:
   - **Forward**: Send to appropriate faculty → Status: `UNDER_REVIEW` (Automatically assigned to YGK)
   - **Return**: Send back to student for corrections → Status: `RETURNED`

### 3. Dean's Office Staff Flow

1. **Login**: Use `dean_eng` or `dean_arch`
2. **View Forwarded Applications**: See applications within faculty scope
3. **Monitor Progress**: View applications currently under YGK review (Read-Only)
4. **Final Decision** (after YGK finalizes):
   - **Approve**: Grant transfer → Status: `APPROVED`
   - Only `FINALIZED` applications can be approved.

### 4. YGK Member Flow

1. **Login**: Use `ygk_cse`, `ygk_mech`, or `ygk_arch`
2. **View Assigned Applications**: See department-specific applications
3. **Evaluate**: Mark as eligible/ineligible (Draft Mode) → Status remains `UNDER_REVIEW`
4. **Generate Ranking**: View auto-calculated rankings for eligible candidates
5. **Finalize**: Submit all evaluations to Dean → Status: `FINALIZED` or `REJECTED`
6. **Export**: Download ranking list as PDF or Excel

### 5. Admin Flow

1. **Login**: Use `admin`
2. **User Management**:
   - Create new users with specific roles
   - Assign faculty/department scopes to Dean's Office Staff and YGK members
   - Update user roles and assignments
   - View all users with color-coded role badges
3. **System Overview**: Access all dashboards and features

## 9. Features

### Security Features

- ✅ JWT-based stateless authentication (30-minute expiration)
- ✅ BCrypt password hashing
- ✅ Password complexity requirements (SEC-09)
- ✅ Role-based access control (RBAC)
- ✅ Scope-based data isolation for Dean's Office and YGK

### Application Features

- **Student Self-Registration**: Secure account creation with TCKN validation.
- **Profile Completion Flow**: Mandatory profile completion before application access.
- **Composite score calculation (PR-07)**: (Converted GPA × 0.5) + (YKS × 0.5).
- **YKS score validation**: Integrated with OSYM service.
- ✅ GPA threshold enforcement (minimum 2.50)
- ✅ Disciplinary record checking
- ✅ Duplicate application prevention
- ✅ Document upload with type validation (PDF only)
- ✅ Auto-generated rankings with quota management
- ✅ Tie-breaking logic (PR-09)

### Admin Panel Features

- ✅ User creation and management
- ✅ Dynamic faculty/department assignment
- ✅ Auto user type assignment based on role
- ✅ Color-coded role badges for easy identification
- ✅ Administrative profile auto-management
- ✅ Audit logging for all actions

### UI Enhancements

- ✅ Real-time status tracking
- ✅ Responsive design with modern aesthetics
- ✅ Dynamic dropdown population
- ✅ Assignment display in user lists
- ✅ Differentiated role colors

## 10. API Documentation

For detailed API endpoints, request/response formats, and business rules logic, refer to the `manual.md` file included in the project root.

**Quick Reference:**

- Authentication: `POST /api/auth/login`
- Applications: `/api/applications`
- Student Profile: `/api/student/profile`
- Documents: `/api/documents`
- Evaluations: `/api/evaluations`
- University Structure: `/api/structure`
- Admin: `/api/admin`

## 11. Testing

### Test Data Generation

Generate 50 test students with varied data:

```bash
python3 init_data.py
```

This creates:

- 50 student accounts across 3 departments
- Applications in different workflow stages
- Realistic GPAs (2.5-4.0) and YKS scores (400-520)
- Varied university backgrounds

### Manual Testing

1. **Restart Backend** to clear database
2. **Run init_data.py** to populate data
3. **Login** with different roles to test workflows
4. **Verify** status transitions and calculations

## 12. Troubleshooting

### Login Fails (403/401)

- Ensure the backend is running at `http://localhost:8080`
- Use the correct password: `password123` (default) or `Student123!` (test students)
- JWT tokens expire after 30 minutes; login again if expired

### Database Errors

- The H2 database resets on backend restart
- All changes are lost unless using PostgreSQL
- Re-run `init_data.py` after restart to restore test data

### CORS Issues

- Backend is configured for `http://localhost:5173`
- Ensure frontend runs on the correct port
- Check `SecurityConfig.java` if using different ports

### Password Validation Errors

- Passwords must be at least 8 characters
- Must include: uppercase, lowercase, numbers, special characters
- Example valid password: `Password123!`

### User Creation Fails

- Ensure faculty/department IDs are correct
- DEAN_OFFICE_STAFF requires `facultyId`
- YGK requires `departmentId`
- Other roles should not have these fields

### Styling Issues

- Run `npm install` in utms-frontend
- Ensure `npm run dev` is running
- Clear browser cache if styles don't update

## 13. Project Structure

```
utms-project/
├── utms-backend/
│   ├── src/main/java/com/iztech/utms/
│   │   ├── controller/      # REST API endpoints
│   │   ├── service/         # Business logic
│   │   ├── model/           # JPA entities
│   │   ├── repository/      # Data access layer
│   │   ├── security/        # JWT & authentication
│   │   ├── dto/             # Data transfer objects
│   │   └── payload/         # Request/response classes
│   └── src/main/resources/
│       ├── application.properties
│       └── data.sql         # Seed data
├── utms-frontend/
│   ├── src/
│   │   ├── components/      # React components
│   │   ├── services/        # API calls
│   │   ├── App.jsx          # Main app component
│   │   └── index.css        # Global styles
│   └── package.json
├── init_data.py             # Test data generator
├── credentials.txt          # All login credentials
├── manual.md               # API documentation
└── README.md               # This file
```

## 14. Development Notes

- **Database**: H2 in-memory resets on restart; use PostgreSQL for persistence
- **Passwords**: BCrypt hashed with strength factor 10
- **JWT**: Signed with HS256 algorithm
- **Sessions**: Completely stateless (no server-side sessions)
- **File Storage**: Local filesystem in `uploads/` directory
- **Seed Data**: Automatically loaded from `data.sql` on startup

---

## 15. Support & Contact

For questions or issues:

- Review `manual.md` for API details
- Check `credentials.txt` for login information
- Consult SRS and SDD documents for requirements

---

© 2026 IZTECH Team 3  
Course: SEDS 505 - Software Engineering Design Studio
