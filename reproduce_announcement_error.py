import requests

BASE_URL = "http://localhost:8080/api"

def login(username, password):
    resp = requests.post(f"{BASE_URL}/auth/login", json={"username": username, "password": password})
    if resp.status_code == 200:
        return resp.json()["token"]
    else:
        print(f"Login failed: {resp.text}")
        return None

def create_announcement(token):
    headers = {"Authorization": f"Bearer {token}"}
    # Multipart form data
    # Priority "NORMAL" is what the frontend sends (based on value="NORMAL")
    data = {
        "title": "x",
        "content": "x",
        "priority": "NORMAL"
    }
    # empty file
    files = {
        'file': (None, '') 
    }
    
    print("Sending POST request to /oidb/announcements...")
    resp = requests.post(f"{BASE_URL}/oidb/announcements", headers=headers, data=data, files=files)
    
    print(f"Status Code: {resp.status_code}")
    print(f"Response Body: {resp.text}")

if __name__ == "__main__":
    # Use an invalid token to test 401 response
    token = "INVALID_TOKEN_123" 
    create_announcement(token)
