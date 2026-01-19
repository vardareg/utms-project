# Undergraduate Transfer Management

# System (UTMS) - Software Requirements

# Specification

## 1. Overall Description

### 1.1 Product Perspective

The Undergraduate Transfer Management System (UTMS) is a web-based information system designed
to automate the undergraduate transfer (yatay geçiş) process at IZTECH. It replaces manual, paper-based
procedures with a centralized, digital workflow that connects Students, the ÖİDB (Student Affairs
Office), the Dean's Office, and the YGK (Transfer Commission). The system ensures accurate eligibility
verification, ranking, and operations while providing transparency and efficiency.

### 1.2 Product Functions

```
UTMS is a secure, web-based information system enabling:
```

```
● Digital submission and tracking of transfer applications;
● Automated validation of academic eligibility and YKS exam scores;
● Controlled routing of applications between ÖİDB, Dean’s Office, and departmental YGK;
● Transparent decision publication and audit logging.
```

The system supports:

```
● Students – submit and track applications;
● ÖİDB Officers – verify, return, or forward applications;
● Dean’s Office Staff – assign applications to departments;
● YGK Members – evaluate eligibility, rank, and decide outcomes.
```

### 1.3 Definitions, Acronyms, and Abbreviations

```
Term Definition
```

```
UTMS Undergraduate Transfer Management System
```

```
ÖİDB Öğrenci İşleri Daire Başkanlığı – Student Affairs Office
```

```
YGK Yatay Geçiş Komisyonu – Transfer Commission
```

```
Dean’s Office Faculty-level administrative office managing academic processes
```

```
UBYS University Information System used for academic record exchange
```

```
Intibak Course-equivalence (credit transfer) process
```

```
YÖK Council of Higher Education, defines national GPA conversion rules
```

```
YKS National University Entrance Exam
```

```
FR Functional Requirement
```

```
NFR Non-Functional Requirement
```

### 1.4 References

```
● ders_katalog.pdf
● intibak.pdf
● kimlik.pdf
● ogrenci_belgesi.pdf
● osym_puan.pdf
● osym_yerlestirme.pdf
● transkript.pdf
● yabanci_dil.pdf
● IEEE Std 830-1998 – Recommended Practice for Software Requirements Specifications
● ISO/IEC/IEEE 29148:2018 – Systems and Software Engineering – Requirements Engineering
```

### 1.5 Overview

```
The document defines:
```

```
● System context and actors (Section 2)
● Detailed functional and non-functional requirements (Sections 3 & 4)
● Structured use-case specifications (Section 5)
● Interface and quality requirements (Section 6)
● Traceability, glossary, and revision control (Section 7)
```

## 2. System Overview

### 2.1 Product Perspective

UTMS operates as a stand-alone web application within IZTECH’s intranet, integrated with UBYS via
secure API for student data exchange.
It replaces fragmented manual workflows with a digital sequence:

Actors & Subsystems:

```
● Student Portal – Application submission & tracking
● ÖİDB Panel – Validation & routing
● Dean’s Panel – Forwarding to departments
● YGK Panel – Evaluation & ranking
● Announcement Module – Public results
● System Administrator – System management
```

_Product Context Diagram_

_Product Use Case Diagram_

### 2.2 Product Functions

```
● Online application creation, editing, and submission
● Secure document upload (transcript, YKS certificate, proficiency proof)
● Automated GPA conversion and eligibility rules
● Role-based forwarding and decision logging
● Ranking and list generation (primary / waitlist)
● Returned-application workflow for corrections
● Faculty Board approval and final publication
```

### 2.3 User Characteristics

```
Actor Description / Responsibility
```

```
Student Submits applications, uploads documents, tracks status.
```

```
ÖİDB Officer Validates submissions, returns invalid ones, forwards eligible
applications.
```

```
Dean’s Office Staff Assigns applications to relevant YGK.
```

```
YGK Member Evaluates applications based on academic rules and ranks candidates.
```

```
System Administrator Manages users, configuration, and database integrity.
```

### 2.4 Constraints

```
● Comply with IZTECH data-handling regulations and YÖK rules.
● Hosted on servers using HTTPS (SSL/TLS 1.3).
● Integration limited to internal UBYS API.
● Application window open only during announced periods.
```

### 2.5 Assumptions and Dependencies

```
● Users have active institutional credentials.
● A stable internet connection is available.
● Evaluation rules (minimum GPA, quota, scoring) are preconfigured.
● Faculty and department data exist in the system before each cycle.
```

## 3. Specific Requirements

### 3.1 Use Cases

#### 3.1.1 General System Use Cases

**3.1.1.1 Use Case 1: User Login**

**Use Case ID:** UC-SYS-
**Use Case Name:** User Login
**Created By:** TEAM 3
**Date:** 25.10.
**Actor:** Student, ÖİDB Officer, Dean's Office Staff, YGK Member
**Description:** Allows any registered actor to log into the system by authenticating their identity.
**Preconditions:**

1. The actor has a valid username and password.
2. The actor has a device with internet access.
    Postconditions:
3. The system authenticates the actor.
4. The system redirects the actor to their role-appropriate dashboard.
**Normal Course:**
5. The actor navigates to the system's main login page.
6. The system displays "Username" and "Password" input fields and a "Login" button.
7. The actor enters their credentials and clicks the "Login" button.
8. The system validates the entered information against database records. (See: EX.1, EX.2)
9. The system starts the actor's session and redirects them to their role-specific panel (e.g., Student
Dashboard).
**Exceptions:**
● **UC-SYS-01.EX.1:** If the username or password is incorrect, the system displays the message
"Incorrect username or password. Please try again."
● **UC-SYS-01.EX.2:** If the database connection fails, the system displays the message "System error.
Please try again later."

**3.1.1.1.1 User Interface (UC-SYS-01)**

**3.1.1.2 Use Case 2: User Logout**

**Use Case ID:** UC-SYS-
**Use Case Name:** User Logout
**Created By:** TEAM 3
**Date:** 25.10.
**Actor:** Student, ÖİDB Officer, Dean’s Office Staff, YGK Member
**Description:** Allows an authenticated actor to securely log out of the system.

**Preconditions:**

```
● The actor is logged into the system (active session).
```

**Postconditions:**

```
● The actor’s session is terminated.
● The system redirects the actor to the login page.
```

**Normal Course:**

1. The actor clicks the "Logout" button.
2. The system terminates and invalidates the session.
3. The system redirects the actor to the login page.

**Exceptions:**

```
● UC-SYS-02.EX.1: If a logout request fails due to a session timeout, the system automatically
clears session cookies and prompts re-login.
```

**3.1.1.1.2 User Interface (UC-SYS-02)**

**3.1.1.3 Use Case 3: Password Reset**

**Use Case ID:** UC-SYS-
**Use Case Name:** Password Reset
**Created By:** TEAM 3
**Date:** 31.10.
**Actor:** Student, ÖİDB Officer, Dean’s Office Staff, YGK Member, System Administrator

**Description:** Registered UTMS users can securely reset forgotten passwords via "Forgot Password" using
institutional email verification. The system validates the email, generates a one-time token, and allows the
user to set a new password that meets security requirements.

**Preconditions**

1. The user has an existing registered account in UTMS.
2. The user has access to their institutional email address.

**Postconditions**

1. The user’s old password is invalidated.
2. The system updates the user’s credentials in the authentication database.
3. A confirmation email is sent to the user confirming the password change.

**Normal Course (Main Flow)Other Use Case: Successful Password Reset**

1. The user clicks "Forgot Password" on the login page.
2. The system displays the password reset form requesting an institutional email.
3. The user enters their institutional email address and clicks "Submit."
4. The system validates the email format and checks if it exists in the database.
5. If valid, the system generates a unique reset token and stores it with an expiration time (
    minutes).
6. The system sends a password reset email containing a secure link to the user’s email.
7. The user clicks the reset link in the email.
8. The system verifies the token validity and opens the "Reset Password" page.
9. The user enters a new password and confirms it.
10. The system validates the password strength.
11. The user clicks "Save New Password."
12. The system updates the password (hashed and salted), invalidates the token, and confirms
    success.
13. A confirmation email is sent stating, "Your password has been successfully changed."

**Alternative Flows**

**UC-SYS-03.AC.1 – Invalid Email Entered**
● If the user provides an unregistered or improperly formatted email, the system will display the
message: "Email not found. Please enter a valid institutional email."

**UC-SYS-03.AC.2 – Token Expired or Invalid**
● If the reset link is invalid or has expired, the system will display the message: "Reset link is
invalid or has expired. Please request a new one."

**UC-SYS-03.AC.3 – Weak Password**
● If the new password fails to meet the specified policy criteria, the system will highlight the
problem and require corrections before permitting submission.

**Exception Flows**

**UC-SYS-03.EX.1 – Email Delivery Failure**
● If an SMTP or network failure prevents the system from sending the reset email, it will log the
error and display the message: "Unable to send reset email. Please try again later or contact the
administrator."

**UC-SYS-03.EX.2 – Database Error During Update**
● If the password update is unsuccessful, the system will log the error, keep the existing credentials,
and display the message: "Password reset could not be completed. Please try again later."

**Assumptions**
● The system’s email service is active and configured.
● The user’s institutional email is valid and accessible.
● Token expiration duration is configurable by the system administrator.

**3.1.1.1.3 User Interface (UC-SYS-03)**

#### 3.1.2 Student Use Cases

**3.1.2.1 Use Case 1: Submit Transfer Application**

**Use Case ID:** UC-STU-
**Use Case Name:** Submit Transfer Application
**Created By:** TEAM 3
**Date:** October 2025
**Actor:** Student
**Description:** The student submits a transfer application by entering personal and academic information,
uploading required documents, and submitting it online.
**Preconditions:**
● The student is logged in (UC-SYS-01).
● The application window is open.
**Postconditions:**
● The application is recorded and assigned a tracking ID.
● The application is routed to the ÖİDB review queue.
**Normal Course:**

1. The student opens the “Transfer Application” page.
2. The system displays input fields for T.C. ID, GPA, and YKS Score.
3. The student fills all required fields.
4. The student uploads required documents (Transcript, YKS Result, Language Proof).
5. The system validates file formats and size. (See: AC.1)
6. The student clicks the “Submit Application” button.
7. The system saves the application, assigns a Tracking ID, and routes it to ÖİDB.
8. A confirmation message appears: “Your application has been successfully received. Tracking ID:
    UTMS-2025-XXXX.”
**Alternative Course:**
● UC-STU-01.AC.1: If validation fails (e.g., missing field or wrong file type), the system highlights
the error and prompts correction before resubmission.
**Exceptions:**
● UC-STU-01.EX.1: If the database or server is unavailable, the system displays “System error.
Please try again later.”
**Assumptions:**
● The student has stable internet access.

**3.1.2.2.1 User Interface (UC-STU-01)**

**3.1.2.2 Use Case 2: Resubmit Returned Application**

**Use Case ID:** UC-STU-
**Use Case Name:** Resubmit Returned Application
**Created By:** TEAM 3
**Date:** October 2025
**Actor:** Student
**Description:** Enables a student to edit and resubmit an application previously returned by ÖİDB.

**Preconditions:**

```
● Application status is “Returned.”
● The correction reason is displayed to the student.
```

**Postconditions:**

```
● The application status changes to “Resubmitted.”
```

**Normal Course:**

1. The student logs in and accesses their returned application.
2. The system displays the return reason entered by ÖİDB.
3. The student corrects data or uploads revised documents.
4. The student clicks “Resubmit.”
5. The system validates and routes the application back to ÖİDB.

**Exceptions:**

```
● UC-STU-02.EX.1: If upload or validation fails, the system displays an error and prevents
submission.
```

**Assumptions:**

```
● The correction note provided by ÖİDB clearly specifies the issue.
```

**3.1.2.2.2 User Interface (UC-STU-02)**

#### 3.1.3 ÖİDB (Student Affairs Office) Use Cases

**3.1.3.1 Use Case 1: Manage and Forward Applications
Use Case ID:** UC-OIDB-01
**Use Case Name:** Manage and Forward Applications
**Created By:** TEAM 3
**Date:** October 2025
**Actor:** ÖİDB Officer
**Description:** The ÖİDB officer reviews submitted applications, verifies integrity, and forwards eligible
ones to the appropriate faculty.

**Preconditions:**
● Applications are submitted by students.
● The ÖİDB officer is logged in.
**Postconditions:**
● Application status changes to “Forwarded to Faculty.”
● An audit record of the action is logged.
**Normal Course:**

1. The officer opens the “Incoming Applications” page.
2. The system lists new applications (ID, Name, Faculty).
3. The officer selects and reviews application details and documents.
4. If valid, clicks “Forward to Faculty.”
5. The system updates the status and creates a log entry.
**Alternative Course:**
● UC-OIDB-01.AC.1: If the application has missing or invalid documents, the officer selects
“Return to Student for Correction,” adds a reason, and submits. The student is notified
automatically.
**Exceptions:**
● UC-OIDB-01.EX.1: If a database timeout occurs, the system displays “Error: Application could
not be forwarded. Please try again.”

**3.1.3.1.1 User Interface (UC-OIDB-01)**

**3.1.3.2 Use Case 2: Publish Announcement**

**Use Case ID:** UC-OIDB-02
**Use Case Name:** Publish Announcement
**Created By:** TEAM 3
**Date:** October 2025
**Actor:** ÖİDB Officer
**Description:** Allows ÖİDB to upload final approved lists and publish results.

**Preconditions:**

```
● Dean’s Office approval has been received.
```

**Postconditions:**

```
● Final results are published to the Announcement Module.
● Students are notified via email and portal.
```

**Normal Course:**

1. ÖİDB officer opens the “Announcements” section.
2. Uploads the final approved list.
3. Clicks “Publish Results.”
4. The system updates status and sends notifications.

**Exceptions:**

```
● UC-OIDB-02.EX.1: If the system fails to send notifications, an alert appears: “Error: The
notification could not be sent. Please try again.”
```

**Assumptions:**

```
● Internet and database are available.
```

**3.1.3.1.1 User Interface (UC-OIDB-02)**

#### 3.1.4 Dean’s Office Use Cases

**3.1.4.1 Use Case 1: Forward to Department Commission**

**Use Case ID:** UC-DEAN-01
**Use Case Name:** Forward to Department Commission
**Created By:** TEAM 3
**Date:** October 2025
**Actor:** Dean’s Office Staff
**Description:** Transfers applications received from ÖİDB to the respective departmental YGK for
evaluation.

**Preconditions:**
● Applications have been received from ÖİDB.
● Relevant YGK exists for each department.
**Postconditions:**
● Applications appear in YGK evaluation queue.
**Normal Course:**

1. Dean’s Office staff opens “Faculty Queue.”
2. Selects applications.
3. Chooses target YGK from dropdown.
4. Clicks “Forward to YGK.”
5. The system confirms and updates the status to “Under Review.”
**Exceptions:**
● UC-DEAN-01.EX.1: If no YGK is assigned, the system displays “Error: No YGK defined for this
department.”
**Assumptions:**
● All departments and YGK members are active in the system.

**3.1.4.1.1 User Interface (UC-DEAN-01)**

**3.1.4.2 Use Case 2: Approve Final List**

**Use Case ID:** UC-DEAN-02
**Use Case Name:** Approve Final List
**Created By:** TEAM 3
**Date:** October 2025
**Actor:** Dean’s Office Staff
**Description:** Allows the Dean’s Office to review, approve, or return the final ranked list prepared by the
YGK before publication.
**Preconditions:**
● The YGK has finalized the evaluation and ranking list.
● The approved list has been sent to the Dean’s Office.
**Postconditions:**
● Approved lists are transmitted to the ÖİDB for publication.
● Audit logs are recorded for the approval action.
**Normal Course:**

1. The Dean’s Office user opens the “Final Lists” panel.
2. The system displays department-level ranked lists (Primary, Waitlist, Rejected).
3. The user reviews the lists for accuracy and compliance.
4. The user selects **Approve** or **Return for Revision**.
5. If approved, the system records the approval and sends the data to the ÖİDB queue.
6. A confirmation message appears: “List approved and sent to ÖİDB.”
**Alternative Course:**
● UC-DEAN-02.AC.1: If minor corrections are needed, the Dean’s Office can attach a comment
and send the list back to the YGK for revision before approval.
**Exceptions:**
● UC-DEAN-02.EX.1: If the database connection fails during submission, the system displays:
“Error: Approval could not be saved. Please try again.”
**Assumptions:**
● The Dean’s Office staff has appropriate permissions and digital signature access to approve
results.

**3.1.4.1.1 User Interface (UC-DEAN-02)**

#### 3.1.5 YGK (Transfer Commission) Use Cases

**3.1.5.1 Use Case 1: Evaluate Applications**

**Use Case ID:** UC-YGK-01
**Use Case Name:** Evaluate Applications
**Created By:** TEAM 3
**Date:** October 2025
**Actor:** YGK Member
**Description:** The YGK evaluates applications based on predefined eligibility and ranking rules, and
generates final decisions.
**Preconditions:**
● Applications received from Dean’s Office.
● Evaluation rules configured (see Section 4.3).
**Postconditions:**
● Ranked list (Primary, Waitlist, Rejected) generated.
**Normal Course:**

1. The YGK member opens the “Evaluation Panel.”
2. The system lists all assigned applications.
3. The system auto-checks eligibility (min GPA, valid YKS, English proof).
4. Member reviews documents manually.
5. Member confirms or overrides automatic eligibility.
6. After all applications are reviewed, member clicks “Finalize Ranking.”
7. The system ranks accepted students according to GPA and quota and sends results to Dean’s
    Office.
**Alternative Course:**
● UC-YGK-01.AC.1: If the YKS score cannot be verified automatically, the system flags “Manual
Verification Required.”
**Exceptions:**
● UC-YGK-01.EX.1: If GPA conversion data is missing, the system displays “Error: GPA
conversion rule not found. Please evaluate manually.”
**Assumptions:**
● All evaluation rules are active in the system configuration.

**3.1.5.1.1 User Interface (UC-YGK-01)**

#### 3.1.6 Administrator Use Cases

**3.1.6.1 Use Case 1: Manage Rules & Users**

```
Use Case ID: UC-ADM-01
Use Case Name: Manage Rules & Users
Created By: TEAM 3
Date: October 2025
Actor: System Administrator
Description: Enables the system administrator to manage user accounts, roles, and predefined eligibility
and evaluation rules in the UTMS Admin Console.
Preconditions:
● The administrator is authenticated and authorized with admin privileges.
Postconditions:
● Updated configuration and user data are saved and immediately applied.
● An audit log entry is created for each modification.
Normal Course:
```

1. The Administrator logs into the Admin Console.
2. The system displays management modules:
    ○ **User Management** (create, edit, deactivate accounts)
    ○ **Rule Management** (update predefined rules PR-01 to PR-24)
    ○ **Access Control (RBAC)** configuration
    ○ **System Monitoring** (logs, backups, performance stats)
3. The administrator selects a module (e.g., “Eligibility Rules”).
4. Edits or adds configuration values (e.g., GPA threshold = 2.50).
5. Clicks “Save Changes.”
6. The system validates input, updates database tables, and logs the action.
7. A confirmation message appears: “Changes saved successfully.”
**Alternative Course:**
● UC-ADM-01.AC.1: If invalid data (e.g., negative GPA threshold) is entered, the system displays:
“Invalid input. Please enter a valid numerical value.”
**Exceptions:**
● UC-ADM-01.EX.1: If database or configuration file access fails, the system shows: “Error:
Unable to save configuration. Please check the system logs.”
**Assumptions:**
● Only administrators with proper authorization tokens can modify rule definitions.
● Backup processes are in place before configuration changes are applied.

**3.1.5.1.1 User Interface (UC-ADM-01)**

### 3.2 External Interfaces

The UTMS provides separate, role-based graphical interfaces that are accessible through a secure web
browser.

```
Interface Description Main Components / Features
```

```
Student Portal Allows students to submit, view, and
track their transfer applications.
```

- Login/Logout page- Application Form
(with validation and upload fields)-
Status Dashboard- Notifications &
Result View

```
ÖİDB Officer
Panel
```

```
Used by Student Affairs officers to
validate and forward applications,
manage audit logs, and publish
announcements.
```

- Incoming Applications Table-
Return/Forward Buttons- Comment
Entry Box- Result Publishing Module

```
Dean’s Office
Panel
```

```
Used to assign applications to
departmental commissions and
approve final results.
```

- Faculty Queue- YGK Assignment
Dropdown- Approval Button- Audit
Log View

```
YGK Evaluation
Panel
```

```
Enables Transfer Commission
members to perform eligibility
checks, scoring, and ranking.
```

- Application Review Page- Eligibility
Validation Section- Auto-Scoring &
Ranking Functions- Finalization Button

```
Announcement
Module (Public)
```

```
Displays published results for
students and the public.
```

- Search by Student ID or Department-
Downloadable Result Lists- Sorting &
Filtering Options

```
Administrator
Console
```

```
Restricted to system administrators
for configuration and user
management.
```

- User Management- Access Control
Settings (RBAC)- Rule Editor (GPA,
YKS thresholds, quotas)- Database &
Log Monitoring

### 3.3 Logical Database Requirements (ER Diagram)

## 4. General Quality and Design Requirements

### 4.1 Performance Requirements

The system shall maintain responsiveness and reliability under expected workloads. Performance
requirements are defined to ensure fast user interaction and efficient data processing.

```
Requirement ID Description Target / Measurement Verification Method
```

```
PRF-01 Page Load Time All user-facing pages
(Login, Dashboard,
Application Form) shall
load in ≤ 3 seconds
under 100 concurrent
users.
```

```
Performance Test
```

```
PRF-02 Data Submission Time Application
submissions and
document uploads shall
complete in ≤ 5
seconds.
```

```
Test
```

```
PRF-03 Database Query
Response
```

```
Common queries (e.g.,
status lookup, list
retrieval) shall return
results in ≤ 2 seconds.
```

```
Test
```

```
PRF-04 System Throughput The system shall handle
a minimum of 1500
simultaneous active
sessions without
degradation exceeding
10% response delay.
```

```
Load Test
```

```
PRF-05 Availability System uptime shall be
≥ 99.5% during
business hours and ≥
99% overall.
```

```
Monitoring / Audit
```

```
PRF-06 Data Backup &
Recovery
```

```
Database backup shall
occur nightly , and
recovery time objective
(RTO) shall be ≤ 2
hours.
```

```
Inspection
```

### 4.2 Other Requirements

#### 4.2.1 Security Requirements

UTMS must ensure the confidentiality, integrity, and availability of data in compliance with institutional
and national data protection policies.

```
Requirement ID Description Target / Standard Verification Method
```

```
SEC-01 Role-Based Access
Control (RBAC)
```

```
Access permissions
must be enforced by
user role (Student,
ÖİDB, Dean, YGK,
Admin).
```

```
Test
```

```
SEC-02 Authentication Login shall use secure
session management
and token-based
authentication (OAuth
2.0 + JWT).
```

```
Inspection
```

```
SEC-03 Password Storage All passwords shall be
hashed and salted using
PBKDF2 or bcrypt with
minimum 10 iterations.
```

```
Code Review
```

```
SEC-04 Data Encryption All data in transit via
HTTPS (TLS 1.3) and
data at rest encrypted
with AES-256.
```

```
Security Audit
```

```
SEC-05 Audit Logging All user actions (login,
submission, approval,
publication) must be
timestamped and stored
in immutable audit
logs.
```

```
Inspection
```

```
SEC-06 Session Timeout User sessions expire
after 30 minutes of
inactivity.
```

```
Test
```

```
SEC-07 Intrusion Detection The system shall log
unauthorized access
attempts and notify
```

```
Test
```

```
administrators.
```

**SEC-08 Backup Security** Backup files encrypted
and accessible only by
admin-level users.

```
Inspection
```

**SEC-09 Password Complexity** Passwords shall be at
least 8 characters long,
include uppercase,
lowercase, numbers,
and special characters.

```
Code Review
```

#### 4.2.2 Usability Requirements

The UTMS interface must ensure clarity, accessibility, and consistency across platforms to minimize user
errors and training needs.

```
Requirement ID Description Target / Metric Verification Method
```

```
USE-01 Responsiveness The system shall
maintain full
functionality on
desktop, tablet, and
mobile devices.
```

```
Test
```

```
USE-02 Accessibility
Compliance
```

```
UI design must
conform to WCAG 2.1
Level AA standards.
```

```
Inspection
```

```
USE-03 Ease of Navigation ≥ 90% of test
participants must
successfully complete
application submission
in ≤ 10 minutes without
assistance.
```

```
Usability Test
```

```
USE-04 Error Feedback All validation errors
shall display
user-friendly messages
(e.g., “File exceeds 5
MB limit”).
```

```
Test
```

```
USE-05 Interface Consistency All pages follow a
unified design system
(color scheme,
typography, button
layout).
```

```
Inspection
```

```
USE-06 Browser
Compatibility
```

```
Fully functional on
Chrome version 142,
Firefox ESR 128
```

```
Test
```

#### 4.2.3 Maintainability Requirements

UTMS must be modular, configurable, and easily maintainable to support evolving policies and
workflows.

```
Requirement ID Description Target / Standard Verification Method
```

```
MAI-01 Modularity Code shall be organized
into separate modules
(Authentication,
Application,
Evaluation,
Announcement).
```

```
Code Review
```

```
MAI-02 Configuration
Management
```

```
All system rules (GPA
threshold, quotas,
deadlines) stored in
editable configuration
files or database tables
— not hard-coded.
```

```
Inspection
```

```
MAI-03 Documentation All modules
documented with inline
comments and updated
API specifications.
```

```
Review
```

```
MAI-04 Error Handling Centralized
error-logging system
captures and classifies
all runtime errors for
diagnosis.
```

```
Test
```

```
MAI-05 Update & Deployment System updates
deployable with zero
downtime via CI/CD
pipeline.
```

```
Test
```

```
MAI-06 Maintainability Index Code maintainability
index ≥ 80 (based on
static analysis tools).
```

```
Analysis
```

#### 4.2.4 Scalability and Portability

```
Requirement ID Description Target / Standard Verification Method
```

```
SCP-01 Scalability The system shall
support increased user
load (up to 2000
concurrent users) with
≤ 15% performance
loss.
```

```
Load Test
```

```
SCP-02 Portability UTMS shall operate on
both Linux and
Windows servers with
no code modification.
```

```
Test
```

```
SCP-03 Cloud Readiness System deployable in
containerized
environment
(Docker/Kubernetes)
for future scaling.
```

```
Analysis
```

```
SCP-04 Integration Flexibility REST API endpoints
compatible with
existing university
systems (UBYS,
YÖKSİS, ÖSYM).
```

```
Test
```

### 4.3 Pre-Defined Rules

#### 4.3.1 Eligibility Rules

```
Rule ID Description Example Condition
```

```
PR-01 Minimum GPA rule Student’s GPA (converted to 4.0 scale) ≥ 2.50
```

```
PR-02 Enrollment type rule Transfer type ∈ {Internal, External, Central Placement}
```

```
PR-03 English proficiency rule TOEFL ≥ 70 OR IZTECH English Proficiency Exam =
Pass
```

```
PR-04 Disciplinary record check Must have “No active disciplinary penalty”
```

```
PR-05 Application period validity Current date within announced transfer window
```

```
PR-06 Course completion rule ≥ 1 completed semester with GPA available
```

#### 4.3.2 Scoring & Ranking Rules

```
Rule ID Description Formula / Criteria
```

PR-07 **Composite Transfer Score
Calculation**

```
Transfer Score = (GPA * 0.5) + (YKS Score * 0.5)
```

PR-08 **Quota Rule (Per Department)** (^) Accept ≤ department_quota
PR-09 **Tie-breaking Rule** Higher YKS Score → Higher GPA → Earliest
Submission Time
PR-10 **Ranking Cutoff Rule** Generate “Primary List” = Top N; “Waitlist” = Next
N×0.5

#### 4.3.3 Validation Rules

```
Rule ID Description Validation Check
```

```
PR-11 File Format Validation Uploaded Transcript must be .PDF ≤ 5 MB
```

```
PR-12 Mandatory Field Validation T.C. No, Faculty, and GPA fields cannot be empty
```

```
PR-13 Data Integrity Check Numeric fields only in GPA/YKS fields
```

```
PR-14 Document Authenticity Compare uploaded YKS code with ÖSYM web API
```

```
PR-15 Duplicate Prevention Reject multiple submissions from same student ID
```

#### 4.3.4 Evaluation & Decision Rules

```
Rule ID Description Action
```

```
PR-16 Auto-Eligibility Determination Mark “Eligible” or “Not Eligible” in YGK Panel
```

```
PR-17 Quota Enforcement System blocks acceptance beyond defined quota
```

```
PR-18 Auto-Ranking Finalization System generates “Asil/Yedek” (Primary/Waitlist) list
```

```
PR-19 Decision Lock Rule YGK cannot edit ranking after final approval
```

```
PR-20 Audit Logging Rule Every YGK action (decision/save) is timestamped
```

#### 4.3.5 Announcement & Publication Rules

```
Rule ID Description Example
```

```
PR-21 Result Visibility Rule Students can only view their own status until official
publication
```

```
PR-22 Publication Format Rule Results exported as .PDF and .XLSX, stored in
Announcement Module
```

```
PR-23 Notification Trigger Rule Send email/SMS upon status change in 5 minutes
```

```
PR-24 Archival Rule Archive published results after 90 days
```

### 4.4 Requirements Traceability Matrix

```
FR ID Functional Requirement Linked NFR(s) Linked
Use
Case(s)
```

```
Verification
Method
```

```
FR-01 The system shall allow users (Students, ÖİDB,
Dean, YGK) to log in using secure institutional
credentials.
```

##### SEC-02, SEC-06, USE-01 UC-SYS-

##### 01

```
Test /
Inspection
```

```
FR-02 The system shall allow users to securely log out and
invalidate session tokens.
```

##### SEC-02, SEC-06 UC-SYS-

##### 02

```
Test
```

```
FR-03 The system shall enable students to create and
submit a transfer application with required data and
documents.
```

##### USE-03, PRF-01, PRF-02 UC-STU-

##### 01

```
Test
```

```
FR-04 The system shall validate uploaded files (type, size,
authenticity).
```

##### PR-11–PR-14, SEC-05 UC-STU-

##### 01

```
Test /
Inspection
```

```
FR-05 The system shall allow students to view and correct
returned applications.
```

##### USE-03, MAI-02 UC-STU-

##### 02

```
Test
```

```
FR-06 ÖİDB officers shall review submitted applications
and either return or forward them to faculties.
```

##### SEC-01, MAI-01 UC-OIDB

##### -01

```
Inspection /
Test
```

```
FR-07 Dean’s Office shall forward received applications to
the appropriate YGK department.
```

##### SEC-01, MAI-02 UC-DEA

##### N-01

```
Test
```

```
FR-08 YGK members shall evaluate applications and
perform automated eligibility checks (GPA, YKS,
proficiency).
```

##### PR-01–PR-06, PRF-03 UC-YGK-

##### 01

```
Test /
Analysis
```

```
FR-09 The system shall generate ranked candidate lists
(Primary/Waitlist) based on scoring rules.
```

##### PR-07–PR-10, PRF-04 UC-YGK-

##### 01

```
Test
```

```
FR-10 Dean’s Office shall approve or return final lists
before publication.
```

##### SEC-05, MAI-02 UC-DEA

##### N-02

```
Inspection /
Test
```

```
FR-11 ÖİDB shall publish approved results and send
notifications to students.
```

##### SEC-08, USE-04 UC-OIDB

##### -02

```
Test
```

```
FR-12 The system shall record all user actions (submission,
forwarding, evaluation, approval) in immutable audit
logs.
```

```
SEC-05, MAI-04 All UCs Inspection /
Audit
```

FR-13 The system shall provide public access to final
results via Announcement Module with search/filter
functions.

##### USE-06, SCP-04 UC-OIDB

##### -02

```
Test
```

FR-14 The Administrator shall manage users, roles, and
system configuration (GPA thresholds, quotas).

##### SEC-01, MAI-01, MAI-02 UC-ADM

##### -01

```
Inspection /
Test
```

FR-16 The system shall integrate with UBYS and ÖSYM
APIs for data validation.

##### SCP-04, SEC-02 UC-YGK-

##### 01

```
Integration
Test
```

FR-17 The system shall prevent duplicate submissions by
the same student ID.

##### PR-15, SEC-05 UC-STU-

##### 01

```
Test
```

FR-18 The system shall restrict access based on user role
(RBAC).

```
SEC-01 All UCs Security
Test
```

FR-19 The system shall provide error feedback messages
for invalid inputs or failed actions.

```
USE-04 All UCs Usability
Test
```

FR-20 The system shall generate audit and performance
reports for administrative review.

##### PRF-05, MAI-04 UC-ADM

##### -01

```
Analysis
```
