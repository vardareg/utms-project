import requests
import json

BASE_URL = "http://localhost:8080/api"

def login(username, password):
    try:
        resp = requests.post(f"{BASE_URL}/auth/login", json={"username": username, "password": password})
        if resp.status_code == 200:
            return resp.json()['token']
        else:
            print(f"Login failed: {resp.text}")
            return None
    except Exception as e:
        print(f"Login error: {e}")
        return None

def check_structure():
    token = login("admin", "password123")
    if not token:
        return

    headers = {"Authorization": f"Bearer {token}"}
    
    print("\n--- Checking Faculties ---")
    try:
        resp = requests.get(f"{BASE_URL}/structure/faculties", headers=headers)
        print(f"Status: {resp.status_code}")
        print(f"Data: {resp.json()}")
    except Exception as e:
        print(f"Failed to fetch faculties: {e}")

    print("\n--- Checking Departments ---")
    try:
        resp = requests.get(f"{BASE_URL}/structure/departments", headers=headers)
        print(f"Status: {resp.status_code}")
        print(f"Data: {resp.json()}")
    except Exception as e:
        print(f"Failed to fetch departments: {e}")

if __name__ == "__main__":
    check_structure()
