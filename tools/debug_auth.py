import requests
import json
import traceback

BASE_URL = "http://localhost:8080/api"

def test_flow():
    # 1. Login
    print("--- 1. Logging in ---")
    try:
        resp = requests.post(f"{BASE_URL}/auth/login", json={"username": "student", "password": "password123"})
        print(f"Status: {resp.status_code}")
        if resp.status_code != 200:
            print(resp.text)
            return
        
        data = resp.json()
        token = data.get("token")
        print(f"Token acquired. Role: {data.get('role')}")
    except Exception as e:
        print(f"Login failed: {e}")
        return

    headers = {"Authorization": f"Bearer {token}"}

    # 2. GET Profile
    print("\n--- 2. GET Profile ---")
    try:
        resp = requests.get(f"{BASE_URL}/student/profile", headers=headers)
        print(f"Status: {resp.status_code}") # Expect 204 or 200
        print(f"Body: {resp.text}")
    except Exception as e:
        print(f"GET failed: {e}")

    # 3. POST Profile
    print("\n--- 3. POST Profile ---")
    payload = {
        "tckn": "11111111111",
        "currentUniversity": "Test Uni",
        "currentProgram": "Test Prog",
        "overallGpa": 3.50
    }
    try:
        resp = requests.post(f"{BASE_URL}/student/profile", json=payload, headers=headers)
        print(f"Status: {resp.status_code}")
        print(f"Body: {resp.text}")
    except Exception as e:
        print(f"POST failed: {e}")

if __name__ == "__main__":
    test_flow()
