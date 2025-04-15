# Mock Consent + File Upload Backend

This project is a secure FastAPI-based mock backend simulating a consent-aware medical data service. It includes:

- JWT-based mock authentication
- User consent tracking
- File upload with metadata
- Audit logging
- Device-aware metadata
- Fully RESTful API

---

## Running the Backend

You need Python with pip installed to setup and run this backend-mock

### Install dependencies

```bash
pip install fastapi uvicorn PyJWT
```

### Run the app

```bash
uvicorn backend_mock:app --reload
```

> Replace `backend_mock` with your Python filename (without `.py`).

---

## Authentication

### Login to receive a JWT token

```bash
curl -X POST http://localhost:8000/api/auth/token \
  -F username=demo -F password=password
```

This returns:
```json
{
  "access_token": "your_token_here",
  "token_type": "bearer"
}
```

Use this token in all other requests as:
```http
Authorization: Bearer your_token_here
```

## Automatic testing of granting consent flow
To run automatic tests for granting consent flow please run `test_consent_flow.sh`. 
This might be useful when:
- you modify the backend mock
- mobile development is having issues with backend mock
