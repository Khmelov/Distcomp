from fastapi import Depends, HTTPException, status
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from security.jwt_service import decode_token
from typing import Optional

bearer_scheme = HTTPBearer(auto_error=False)  # ← auto_error=False

def get_current_user(credentials: Optional[HTTPAuthorizationCredentials] = Depends(bearer_scheme)):
    if not credentials:
        raise HTTPException(
            status_code=401,
            detail={"errorMessage": "Not authenticated", "errorCode": 40100}
        )
    payload = decode_token(credentials.credentials)
    if not payload:
        raise HTTPException(
            status_code=401,
            detail={"errorMessage": "Invalid or expired token", "errorCode": 40100}
        )
    return payload

def require_admin(user=Depends(get_current_user)):
    if user.get("role") != "ADMIN":
        raise HTTPException(
            status_code=403,
            detail={"errorMessage": "Admin access required", "errorCode": 40300}
        )
    return user

def require_auth(user=Depends(get_current_user)):
    return user