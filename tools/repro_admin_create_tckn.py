
import requests
import json
import sys

BASE_URL = "http://localhost:8080"
ADMIN_USER = "admin"
ADMIN_PASS = "Password123!"

def login(username, password):
    resp = requests.post(f"{BASE_URL}/api/auth/login", json={"username": username, "password": password})
    if resp.status_code != 200:
        print(f"Login failed for {username}: {resp.text}")
        sys.exit(1)
    return resp.json()["token"]

def delete_user_if_exists(token, username):
    # Find user ID
    resp = requests.get(f"{BASE_URL}/api/admin/users", headers={"Authorization": f"Bearer {token}"})
    if resp.status_code == 200:
        users = resp.json()
        for u in users:
            if u["username"] == username:
                print(f"Deleting existing user {username} (ID: {u['id']})...")
                requests.delete(f"{BASE_URL}/api/admin/users/{u['id']}", headers={"Authorization": f"Bearer {token}"})
                return

def create_user_with_tckn(token):
    username = "test_tckn_user"
    payload = {
        "username": username,
        "firstName": "Test",
        "lastName": "Tckn",
        "email": "test_tckn@example.com",
        "password": "Password123!",
        "role": "ROLE_STUDENT",
        "userType": "Student",
        "tckn": "99999999999"  # Valid 11 digit TCKN
    }
    print(f"Creating user {username} with TCKN {payload['tckn']}...")
    resp = requests.post(f"{BASE_URL}/api/admin/users", json=payload, headers={"Authorization": f"Bearer {token}"})
    print(f"Create User Response Code: {resp.status_code}")
    if resp.status_code != 200:
        print(f"Response Body: {resp.text}")
        return False
    return True

def check_profile(username):
    print(f"Logging in as {username} to check profile...")
    token = login(username, "Password123!")
    resp = requests.get(f"{BASE_URL}/api/student/profile", headers={"Authorization": f"Bearer {token}"})
    print(f"Get Profile Response Code: {resp.status_code}")
    if resp.status_code == 200:
        data = resp.json()
        print(f"Profile Data: {data}")
        if data.get("tckn") == "99999999999":
            print("SUCCESS: TCKN found in profile.")
            return True
        else:
            print("FAILURE: Profile found but TCKN mismatch/missing.")
            return False
    elif resp.status_code == 204:
        print("FAILURE: Profile not found (204 No Content).")
        return False
    else:
        print(f"FAILURE: Unexpected status {resp.status_code}")
        return False

def main():
    try:
        admin_token = login(ADMIN_USER, ADMIN_PASS)
        delete_user_if_exists(admin_token, "test_tckn_user")
        if create_user_with_tckn(admin_token):
            check_profile("test_tckn_user")
    except Exception as e:
        print(f"Error: {e}")

if __name__ == "__main__":
    main()
