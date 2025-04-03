from fastapi import FastAPI, UploadFile, File, Form, HTTPException, Header, Depends, Query, Request
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import List, Dict, Any
import uuid
import os
import shutil
import time
import jwt

import logging
from fastapi import FastAPI, Request
from fastapi.middleware.trustedhost import TrustedHostMiddleware
from starlette.middleware.base import BaseHTTPMiddleware

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Allows all origins; customize as needed
    allow_credentials=True,
    allow_methods=["*"],  # Allows all HTTP methods
    allow_headers=["*"],  # Allows all headers
)

SECRET_KEY = "mock-secret"
ALGORITHM = "HS256"
UPLOAD_DIR = "uploads"
os.makedirs(UPLOAD_DIR, exist_ok=True)

# ========== Models ==========
class User(BaseModel):
    userId: str
    username: str
    email: str = ""
    registeredAt: int = int(time.time() * 1000)
    studyIds: List[str] = []

class ConsentDefinition(BaseModel):
    consentId: str = str(uuid.uuid4())
    studyId: str
    version: str = "v1.0"
    document: str = ""
    title: str = "Consent Form Title"
    description: str = "Consent form description and purpose."
    scope: List[str] = ["upload"]

class UserConsent(BaseModel):
    consentId: str
    grantedAt: int = int(time.time() * 1000)
    grantedBy: str = "user"
    acceptedVia: str = "mobile_app"

class FileMetadata(BaseModel):
    fileId: str
    fileName: str
    studyId: str
    uploadedAt: int
    userId: str
    consentVersion: str
    consentId: str
    tags: List[str] = ["document"]
    deviceId: str
    filePath: str

# ========== In-Memory Stores ==========

# Preload realistic medical study consents
consent1_id = str(uuid.uuid4())
consent2_id = str(uuid.uuid4())

consent_definitions: Dict[str, Dict] = {}
user_consents: Dict[str, List[Dict]] = {}
file_meta_store: List[Dict] = []
user_device_registry: Dict[str, set] = {}

consent_definitions.update({
    consent1_id: ConsentDefinition(
        consentId=consent1_id,
        studyId="cardio2025",
        version="v1.0",
        document="https://example.com/docs/cardio-consent-v1.pdf",
        title="Cardiology Study Consent",
        description="Consent form for participation in the Cardio 2025 Study focused on remote ECG data collection and wearable monitoring.",
        scope=["upload", "view"]
    ).dict(),
    consent2_id: ConsentDefinition(
        consentId=consent2_id,
        studyId="neuro2025",
        version="v2.1",
        document="https://example.com/docs/neuro-consent-v2.1.pdf",
        title="Neuroscience Study Consent",
        description="Consent to participate in the Neuro 2025 Study involving cognitive function assessment using mobile applications.",
        scope=["upload"]
    ).dict()
})
user_store: Dict[str, User] = {
    "user123": User(userId="user123", username="demo", email="demo@example.com", studyIds=["cardio2025"]),
    "user456": User(userId="user456", username="other", email="other@example.com", studyIds=["neuro2025"]),
}

def create_token(user_id: str):
    payload = {
        "sub": user_id,
        "iat": int(time.time()),
        "exp": int(time.time()) + 3600
    }
    return jwt.encode(payload, SECRET_KEY, algorithm=ALGORITHM)

def get_current_user(authorization: str = Header(...)) -> User:
    if not authorization.startswith("Bearer "):
        raise HTTPException(status_code=401, detail="Invalid auth header")
    token = authorization.split(" ")[1]
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        user_id = payload["sub"]
        user = user_store.get(user_id)
        if not user:
            raise HTTPException(status_code=404, detail="User not found")
        return user
    except jwt.ExpiredSignatureError:
        raise HTTPException(status_code=401, detail="Token expired")
    except jwt.InvalidTokenError:
        raise HTTPException(status_code=401, detail="Invalid token")

@app.post("/api/auth/token")
def get_token(username: str = Form(...), password: str = Form(...)):
    if (username, password) in [("demo", "password"), ("other", "secret")]:
        for user in user_store.values():
            if user.username == username:
                return {"access_token": create_token(user.userId), "token_type": "bearer"}
    raise HTTPException(status_code=401, detail="Invalid credentials")

@app.get("/api/me")
def get_me(current_user: User = Depends(get_current_user)):
    return current_user

@app.get("/api/consents/me")
def get_user_consents(current_user: User = Depends(get_current_user)):
    accepted = user_consents.get(current_user.userId, [])
    enriched = [
        {
            **c,
            **consent_definitions.get(c["consentId"], {})
        } for c in accepted
    ]
    return {
        "userId": current_user.userId,
        "consents": enriched
    }

@app.get("/api/consents/available")
def get_available_consents(current_user: User = Depends(get_current_user)):
    accepted_ids = {c["consentId"] for c in user_consents.get(current_user.userId, [])}
    available = [
        c for c in consent_definitions.values()
        if c["studyId"] in current_user.studyIds and c["consentId"] not in accepted_ids
    ]
    return {
        "consents": available
    }

@app.post("/api/consents/accept")
def accept_consent(
    consentId: str = Form(...),
    deviceId: str = Form(...),
    current_user: User = Depends(get_current_user)
):
    if consentId not in consent_definitions:
        raise HTTPException(status_code=404, detail="Consent definition not found")

    if any(c["consentId"] == consentId for c in user_consents.get(current_user.userId, [])):
        return {"message": f"Consent {consentId} already accepted by user {current_user.userId}"}

    accepted = UserConsent(consentId=consentId)
    user_consents.setdefault(current_user.userId, []).append(accepted.dict())
    user_device_registry.setdefault(current_user.userId, set()).add(deviceId)

    return {"message": f"Consent {consentId} accepted by user {current_user.userId}"}

@app.post("/api/files/upload")
def upload_file(
    userId: str = Form(...),
    studyId: str = Form(...),
    deviceId: str = Form(...),
    file: UploadFile = File(...),
    current_user: User = Depends(get_current_user)
):
    user_device_registry.setdefault(userId, set()).add(deviceId)

    user_consented_ids = [uc["consentId"] for uc in user_consents.get(userId, [])]
    matched = [(cid, c) for cid, c in consent_definitions.items() if c["studyId"] == studyId and cid in user_consented_ids]
    if not matched:
        raise HTTPException(status_code=403, detail="No valid consent found")

    if any(f["fileName"] == file.filename and f["userId"] == userId and f["studyId"] == studyId for f in file_meta_store):
        raise HTTPException(status_code=409, detail="Duplicate file upload detected")

    consentId, consent = matched[0]
    file_id = str(uuid.uuid4())
    file_path = os.path.join(UPLOAD_DIR, file_id)
    with open(file_path, "wb") as f:
        shutil.copyfileobj(file.file, f)

    metadata = FileMetadata(
        fileId=file_id,
        fileName=file.filename,
        studyId=studyId,
        userId=userId,
        uploadedAt=int(time.time() * 1000),
        consentVersion=consent.get("version", "unknown"),
        consentId=consentId,
        deviceId=deviceId,
        filePath=file_path
    )
    file_meta_store.append(metadata.dict())
    return metadata