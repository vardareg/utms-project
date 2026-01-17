#!/usr/bin/env python3
"""
Initialize comprehensive test data for UTMS application.
Creates 50 students with applications in various stages of the workflow.

Naming convention: student[number]_[dept]_[status]
- dept: cse (Computer Eng), mech (Mechanical Eng), arch (Architecture)
- status: new, forwarded (under_review), evaluated (finalized), approved, rejected
"""

import requests
import json
import time
import random

BASE_URL = "http://localhost:8080"

# Credentials
ADMIN_PASSWORD = "Password123!"
STUDENT_PASSWORD = "Student123!"

# YGK and Dean usernames
YGK_CSE = "ygk_cse"
YGK_MECH = "ygk_mech"
YGK_ARCH = "ygk_arch"
DEAN_ENG = "dean_eng"
DEAN_ARCH = "dean_arch"
OIDB = "oidb"

# Department mappings
DEPT_MAP = {
    "cse": {"id": 1, "name": "Computer Engineering", "ygk": YGK_CSE, "dean": DEAN_ENG},
    "mech": {"id": 2, "name": "Mechanical Engineering", "ygk": YGK_MECH, "dean": DEAN_ENG},
    "arch": {"id": 3, "name": "Architecture", "ygk": YGK_ARCH, "dean": DEAN_ARCH}
}

# Universities for diversity
UNIVERSITIES = [
    "Ege University", "METU", "Boğaziçi University", "İTÜ",
    "Hacettepe University", "Ankara University", "Dokuz Eylül University",
    "Gazi University", "Yıldız Technical University", "Mimar Sinan University",
    "Çukurova University", "Süleyman Demirel University", "Erciyes University",
    "Atatürk University", "Karadeniz Technical University"
]

# Programs for diversity
PROGRAMS = {
    "cse": ["Computer Science", "Computer Engineering", "Software Engineering", "Information Systems"],
    "mech": ["Mechanical Engineering", "Mechatronics", "Manufacturing Engineering", "Industrial Engineering"],
    "arch": ["Architecture", "Interior Architecture", "Urban Planning", "Landscape Architecture"]
}


def generate_students(count=50):
    """Generate student data with varied scores and statuses."""
    students = []
    dept_keys = list(DEPT_MAP.keys())
    
    # Status distribution for 50 students
    statuses = (
        ["new"] * 10 +  # 10 at OIDB
        ["forwarded"] * 10 +  # 10 at YGK (Under Review)
        ["evaluated"] * 10 +  # 10 Finalized (Waiting for Dean)
        ["approved"] * 15 +  # 15 Approved
        ["rejected"] * 5  # 5 Rejected
    )
    
    for i in range(count):
        dept = dept_keys[i % 3]
        status = statuses[i]
        
        # Generate varied scores
        base_gpa = random.uniform(2.8, 4.0)
        base_yks = random.uniform(420, 520)
        
        # Lower scores for rejected students
        if status == "rejected":
            base_gpa = random.uniform(2.5, 3.2)
            base_yks = random.uniform(400, 450)
        
        student = {
            "username": f"student{i+1:02d}_{dept}_{status}",
            "first_name": "Test",
            "last_name": f"Student{i+1}",
            "dept": dept,
            "status": status,
            "gpa": round(base_gpa, 2),
            "yks": round(base_yks, 1),
            "university": random.choice(UNIVERSITIES),
            "program": random.choice(PROGRAMS[dept]),
            "disciplinary": (status == "rejected" and i % 2 == 0)  # Some rejected have disciplinary
        }
        students.append(student)
    
    return students


STUDENTS = generate_students(50)


def login(username, password):
    """Login and get JWT token."""
    response = requests.post(f"{BASE_URL}/api/auth/login", json={
        "username": username,
        "password": password
    })
    if response.status_code == 200:
        return response.json()["token"]
    else:
        raise Exception(f"Login failed for {username}: {response.text}")


def create_student_user(admin_token, username, email, first_name, last_name):
    """Create a student user via admin API."""
    headers = {"Authorization": f"Bearer {admin_token}"}
    user_request = {
        "username": username,
        "email": email,
        "firstName": first_name,
        "lastName": last_name,
        "password": STUDENT_PASSWORD,
        "role": "ROLE_STUDENT",
        "userType": "STUDENT"
    }
    response = requests.post(f"{BASE_URL}/api/admin/users", 
                            json=user_request, 
                            headers=headers)
    return response.status_code in [200, 201]


def create_profile(student_token, student_data):
    """Create student profile."""
    headers = {"Authorization": f"Bearer {student_token}"}
    tckn_base = 12345678900
    tckn = str(tckn_base + STUDENTS.index(student_data) + 1)
    
    profile_data = {
        "tckn": tckn,
        "currentUniversity": student_data["university"],
        "currentProgram": student_data["program"],
        "overallGpa": student_data["gpa"],
        "hasDisciplinaryRecord": student_data.get("disciplinary", False)
    }
    response = requests.post(f"{BASE_URL}/api/student/profile", 
                            json=profile_data, 
                            headers=headers)
    return response.status_code in [200, 201]


def submit_application(student_token, student_data):
    """Submit transfer application."""
    headers = {"Authorization": f"Bearer {student_token}"}
    dept_info = DEPT_MAP[student_data["dept"]]
    
    app_data = {
        "targetDepartmentId": dept_info["id"],
        "yksScore": student_data["yks"]
    }
    response = requests.post(f"{BASE_URL}/api/applications", 
                            json=app_data, 
                            headers=headers)
    if response.status_code in [200, 201]:
        return response.json().get("trackingId")
    return None


def upload_document(student_token, app_id, doc_type):
    """Upload dummy PDF document."""
    headers = {"Authorization": f"Bearer {student_token}"}
    files = {
        'file': ('dummy.pdf', b'%PDF-1.4 dummy content', 'application/pdf')
    }
    data = {
        'applicationId': app_id,
        'documentType': doc_type
    }
    
    response = requests.post(f"{BASE_URL}/api/documents/upload",
                            headers=headers, files=files, data=data)
    if response.status_code != 200:
        print(f"    ! Upload {doc_type} failed for App {app_id}: {response.status_code} - {response.text}")


def forward_application(oidb_token, app_id):
    """OIDB forwards application (Automatically goes to YGK UNDER_REVIEW)."""
    headers = {"Authorization": f"Bearer {oidb_token}"}
    response = requests.patch(f"{BASE_URL}/api/applications/{app_id}/forward", 
                             headers=headers)
    if response.status_code not in [200, 201, 204]:
        print(f"    ! Forward failed for {app_id}: {response.status_code} - {response.text}")
    return response.status_code in [200, 201, 204]


def evaluate_application(ygk_token, app_id, is_eligible=True):
    """YGK submits draft evaluation."""
    headers = {"Authorization": f"Bearer {ygk_token}"}
    payload = {
        "isEligible": is_eligible,
        "note": "Auto-evaluated by init script (Draft)"
    }
    response = requests.post(f"{BASE_URL}/api/evaluations/{app_id}", 
                            json=payload,
                            headers=headers)
    return response.status_code in [200, 201]


def finalize_evaluations(ygk_token, dept_id):
    """YGK Finalizes evaluations for Department."""
    headers = {"Authorization": f"Bearer {ygk_token}"}
    response = requests.post(f"{BASE_URL}/api/evaluations/finalize/{dept_id}", 
                            headers=headers)
    return response.status_code in [200, 204]


def approve_application(dean_token, app_id):
    """Dean approves application."""
    headers = {"Authorization": f"Bearer {dean_token}"}
    response = requests.patch(f"{BASE_URL}/api/applications/{app_id}/approve", 
                             headers=headers)
    if response.status_code not in [200, 201]:
         print(f"    ! Approve failed for {app_id}: {response.status_code} - {response.text}")
    return response.status_code in [200, 201]


def main():
    print("=" * 70)
    print("UTMS COMPREHENSIVE TEST DATA - 50 STUDENTS")
    print("=" * 70)
    
    app_ids = {}
    
    # Step 1: Login as admin
    print("\n[1/7] Logging in as admin...")
    try:
        admin_token = login("admin", "password123")
        print("✓ Admin authenticated")
    except Exception as e:
        print(f"✗ Admin login failed: {e}")
        return
    
    # Step 2: Create student users
    print(f"\n[2/7] Creating {len(STUDENTS)} student users...")
    created_count = 0
    for student in STUDENTS:
        email = f"{student['username']}@external.edu.tr"
        if create_student_user(admin_token, student['username'], email, student['first_name'], student['last_name']):
            created_count += 1
            if created_count % 10 == 0:
                print(f"  ✓ Created {created_count}/{len(STUDENTS)} users...")
    print(f"✓ Successfully created {created_count} student users")
    
    # Step 3: Create profiles, submit applications, and upload docs
    print(f"\n[3/7] Creating profiles, submitting applications, and uploading docs...")
    submitted_count = 0
    for student in STUDENTS:
        try:
            student_token = login(student['username'], STUDENT_PASSWORD)
            if create_profile(student_token, student):
                app_id = submit_application(student_token, student)
                if app_id:
                    # Upload required documents
                    upload_document(student_token, app_id, "TRANSCRIPT")
                    upload_document(student_token, app_id, "YKS_RESULT")
                    upload_document(student_token, app_id, "ENGLISH_PROOF")
                    
                    app_ids[student['username']] = app_id
                    submitted_count += 1
                    if submitted_count % 10 == 0:
                        print(f"  ✓ Submitted {submitted_count}/{len(STUDENTS)} applications...")
        except Exception as e:
            continue
    
    print(f"✓ Successfully submitted {submitted_count} applications")
    
    if not app_ids:
        print("\n⚠️  No applications created. Exiting.")
        return
    
    time.sleep(1)
    
    # Step 4: OIDB forwards applications
    print("\n[4/7] OIDB forwarding applications...")
    try:
        oidb_token = login(OIDB, "password123")
        forward_count = 0
        for student in STUDENTS:
            # All apps not in 'new' state must have been forwarded
            if student['status'] != 'new' and student['username'] in app_ids:
                app_id = app_ids[student['username']]
                if forward_application(oidb_token, app_id):
                    forward_count += 1
        print(f"✓ Forwarded {forward_count} applications (Now UNDER_REVIEW)")
    except Exception as e:
        print(f"⚠️  OIDB forward had issues: {e}")
    
    time.sleep(1)
    
    # Step 5: (SKIPPED) Dean Assign uses to be here. Now automated.
    print("\n[5/7] Dean Assignment (Automated)...")
    print("✓ Skipped manual assignment (handled by forward logic)")

    time.sleep(1)
    
    # Step 6: YGK Evaluates & Finalizes
    print("\n[6/7] YGK evaluating and finalizing...")
    total_evals = 0
    total_finalized_batches = 0
    
    for dept_key, dept_info in DEPT_MAP.items():
        try:
            ygk_token = login(dept_info['ygk'], "password123")
            dept_students = [s for s in STUDENTS if s['dept'] == dept_key]
            
            # 6a. Submit Draft Evaluations
            drafts_count = 0
            for student in dept_students:
                if student['status'] in ['evaluated', 'approved', 'rejected'] and student['username'] in app_ids:
                    app_id = app_ids[student['username']]
                    
                    # Determine eligibility based on target status
                    is_eligible = (student['status'] != 'rejected')
                    
                    if evaluate_application(ygk_token, app_id, is_eligible=is_eligible):
                        drafts_count += 1
            
            total_evals += drafts_count
            
            # 6b. Finalize Department
            # Only if we had drafts to finalize
            if drafts_count > 0:
                if finalize_evaluations(ygk_token, dept_info['id']):
                    total_finalized_batches += 1
                    
        except Exception as e:
            print(f"⚠️  YGK error for {dept_key}: {e}")
            continue
            
    print(f"✓ Submitted {total_evals} draft evaluations")
    print(f"✓ Finalized {total_finalized_batches} department batches")
    
    time.sleep(1)
    
    # Step 7: Dean Approves
    print("\n[7/7] Dean making final decisions...")
    approve_count = 0
    reject_count = 0
    
    for dept_key, dept_info in DEPT_MAP.items():
        try:
            dean_token = login(dept_info['dean'], "password123")
            dept_students = [s for s in STUDENTS if s['dept'] == dept_key]
            
            for student in dept_students:
                app_id = app_ids.get(student['username'])
                if not app_id:
                    continue
                
                if student['status'] == 'approved':
                    if approve_application(dean_token, app_id):
                        approve_count += 1
                elif student['status'] == 'rejected':
                    reject_count += 1
                    
        except Exception as e:
            continue
    
    print(f"✓ Approved {approve_count} applications")
    # Note: Rejected count here corresponds to intended rejections.
    
    # Summary
    print("\n" + "=" * 70)
    print("INITIALIZATION COMPLETE!")
    print("=" * 70)
    
    print(f"\n📊 Created {len(STUDENTS)} students across 3 departments")
    print(f"   • Computer Engineering: {len([s for s in STUDENTS if s['dept'] == 'cse'])} students")
    print(f"   • Mechanical Engineering: {len([s for s in STUDENTS if s['dept'] == 'mech'])} students")
    print(f"   • Architecture: {len([s for s in STUDENTS if s['dept'] == 'arch'])} students")
    
    print(f"\n🔑 Login credentials:")
    print(f"   • All students: {STUDENT_PASSWORD}")
    print(f"   • All staff/admins: password123")
    
    print(f"\n✨ Test different workflows by logging in as:")
    print(f"   • OIDB: {OIDB}")
    print(f"   • YGK: {YGK_CSE}, {YGK_MECH}, {YGK_ARCH}")
    print(f"   • Deans: {DEAN_ENG}, {DEAN_ARCH}")


if __name__ == "__main__":
    main()
