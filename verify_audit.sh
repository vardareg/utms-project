#!/bin/bash

# Authenticate as Dean Engineering
DEAN_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "dean_eng", "password": "password123"}' | jq -r .token)

# Authenticate as YGK CSE
YGK_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "ygk_cse", "password": "password123"}' | jq -r .token)

# Authenticate as Admin
ADMIN_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "password123"}' | jq -r .token)

echo "--- DEAN ENG LOGS (Expect logs from CSE and MECH) ---"
# Show first 5 logs
curl -s -H "Authorization: Bearer $DEAN_TOKEN" http://localhost:8080/api/admin/audit-logs | jq '.[0:5] | .[].details'
# Distinct check for CSE logs to verify faculty scope
echo "- Checking for CSE specific logs for Dean:"
curl -s -H "Authorization: Bearer $DEAN_TOKEN" http://localhost:8080/api/admin/audit-logs | grep "Computer Engineering" | head -n 3

echo "--- YGK CSE LOGS (Expect logs ONLY from CSE) ---"
curl -s -H "Authorization: Bearer $YGK_TOKEN" http://localhost:8080/api/admin/audit-logs | jq '.[0:5] | .[].details'

echo "--- ADMIN LOGS (Expect ALL logs) ---"
curl -s -H "Authorization: Bearer $ADMIN_TOKEN" http://localhost:8080/api/admin/audit-logs | jq 'length'
