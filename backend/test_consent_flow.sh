#!/bin/bash

# HealthAssistant App Backend Mockup Testing Script

# This test script is for testing the consent flow 
# in case any modifications are made in a mockup backend 
# or if mobile app shows issues with consent flow.

echo "HealthAssistant App Backend Mockup Testing Script"
echo "==============================================="

# 1. Get authentication token
echo -e "\n1. Getting authentication token..."
TOKEN=$(curl -s -X POST "http://localhost:8000/api/auth/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=demo&password=password" | jq -r .access_token)

if [ -z "$TOKEN" ]; then
    echo "Failed to get token"
    exit 1
fi
echo "Token received successfully"

# 2. Get available consents
echo -e "\n2. Getting available consents..."
AVAILABLE_CONSENTS=$(curl -s -H "Authorization: Bearer $TOKEN" \
  http://localhost:8000/api/consents/available)
echo "Available consents:"
echo $AVAILABLE_CONSENTS | jq '.'

# 3. Extract first consent ID
CONSENT_ID=$(echo $AVAILABLE_CONSENTS | jq -r '.consents[0].consentId')
if [ -z "$CONSENT_ID" ]; then
    echo "No available consents found"
    exit 1
fi

# 4. Accept the consent
echo -e "\n3. Accepting consent $CONSENT_ID..."
ACCEPT_RESPONSE=$(curl -s -X POST "http://localhost:8000/api/consents/accept" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "consentId=$CONSENT_ID&deviceId=test-device-001")
echo "Accept response:"
echo $ACCEPT_RESPONSE | jq '.'

# 5. Verify accepted consents
echo -e "\n4. Verifying accepted consents..."
ACCEPTED_CONSENTS=$(curl -s -H "Authorization: Bearer $TOKEN" \
  http://localhost:8000/api/consents/me)
echo "My consents:"
echo $ACCEPTED_CONSENTS | jq '.'

echo -e "\nTest completed!" 