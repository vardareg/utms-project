import requests
import json

BASE_URL = "http://localhost:8080/api"

def login(username, password):
    resp = requests.post(f"{BASE_URL}/auth/login", json={"username": username, "password": password})
    if resp.status_code == 200:
        return resp.json()
    else:
        print(f"Login failed for {username}: {resp.text}")
        return None

def seed_data(token, dept_id):
    headers = {"Authorization": f"Bearer {token}"}
    resp = requests.post(f"{BASE_URL}/evaluations/ranking/seed/{dept_id}", headers=headers)
    return resp.status_code

def get_apps(token, status="UNDER_REVIEW"):
    headers = {"Authorization": f"Bearer {token}"}
    resp = requests.get(f"{BASE_URL}/applications/status/{status}", headers=headers)
    if resp.status_code == 200:
        return resp.json()
    return []

def verify():
    # 1. Login Admin and Seed
    print("--- 1. Seeding Data ---")
    admin_auth = login("admin", "password123")
    if not admin_auth: return
    
    print("Seeding Dept 1 (CSE)...")
    seed_data(admin_auth["token"], 1)
    print("Seeding Dept 2 (Mech)...")
    seed_data(admin_auth["token"], 2)

    # 2. Verify Dean CSE (Should see only Dept 1)
    print("\n--- 2. Verifying Dean CSE (Dept 1) ---")
    cse_auth = login("dean_cse", "password123")
    if cse_auth:
        apps = get_apps(cse_auth["token"])
        cse_apps = [a for a in apps if a["targetDepartmentId"] == 1]
        mech_apps = [a for a in apps if a["targetDepartmentId"] == 2]
        print(f"Total Apps Visible: {len(apps)}")
        print(f"CSE Apps: {len(cse_apps)}")
        print(f"Mech Apps: {len(mech_apps)}")
        
        if len(cse_apps) == 15 and len(mech_apps) == 0:
            print("SUCCESS: Dean CSE sees only CSE apps.")
        else:
            print("FAILURE: Dean CSE sees incorrect apps.")

    # 3. Verify Dean Mech (Should see only Dept 2)
    print("\n--- 3. Verifying Dean Mech (Dept 2) ---")
    mech_auth = login("dean_mech", "password123")
    if mech_auth:
        apps = get_apps(mech_auth["token"])
        cse_apps = [a for a in apps if a["targetDepartmentId"] == 1]
        mech_apps = [a for a in apps if a["targetDepartmentId"] == 2]
        print(f"Total Apps Visible: {len(apps)}")
        print(f"CSE Apps: {len(cse_apps)}")
        print(f"Mech Apps: {len(mech_apps)}")

        if len(mech_apps) == 15 and len(cse_apps) == 0:
            print("SUCCESS: Dean Mech sees only Mech apps.")
        else:
            print("FAILURE: Dean Mech sees incorrect apps.")

    # 4. Verify Dean Eng (Faculty 1 - Should see Both)
    print("\n--- 4. Verifying Dean Eng (Faculty 1) ---")
    eng_auth = login("dean_eng", "password123")
    if eng_auth:
        apps = get_apps(eng_auth["token"])
        cse_apps = [a for a in apps if a["targetDepartmentId"] == 1]
        mech_apps = [a for a in apps if a["targetDepartmentId"] == 2]
        print(f"Total Apps Visible: {len(apps)}")
        print(f"CSE Apps: {len(cse_apps)}")
        print(f"Mech Apps: {len(mech_apps)}")

        if len(cse_apps) == 15 and len(mech_apps) == 15:
            print("SUCCESS: Dean Eng sees both.")
        else:
            print("FAILURE: Dean Eng missing apps.")

    # 5. Verify Dean Arch (Faculty 3 - Should see Dept 6 & 7)
    print("\n--- 5. Verifying Dean Arch (Faculty 3) ---")
    
    # Seed Dept 6 & 7 first
    seed_data(admin_auth["token"], 6)
    seed_data(admin_auth["token"], 7)

    arch_auth = login("dean_arch", "password123")
    if arch_auth:
        apps = get_apps(arch_auth["token"])
        arch_apps = [a for a in apps if a["targetDepartmentId"] == 6]
        crp_apps = [a for a in apps if a["targetDepartmentId"] == 7]
        other_apps = [a for a in apps if a["targetDepartmentId"] not in [6, 7]]

        print(f"Total Apps Visible: {len(apps)}")
        print(f"Arch Apps: {len(arch_apps)}")
        print(f"CRP Apps: {len(crp_apps)}")
        print(f"Other Apps: {len(other_apps)}")

        if len(arch_apps) == 15 and len(crp_apps) == 15 and len(other_apps) == 0:
            print("SUCCESS: Dean Arch sees Architecture & CRP apps only.")
        else:
            print("FAILURE: Dean Arch sees incorrect apps.")

if __name__ == "__main__":
    verify()
