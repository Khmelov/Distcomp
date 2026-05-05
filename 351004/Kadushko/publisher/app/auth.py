import jwt
import bcrypt
from datetime import datetime, timedelta
from fastapi import HTTPException
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials

SECRET_KEY = "distcomp-secret-key-2024"
ALGORITHM = "HS256"
ACCESS_TOKEN_EXPIRE_MINUTES = 60

security = HTTPBearer()


def hash_password(password: str) -> str:
    return bcrypt.hashpw(password.encode(), bcrypt.gensalt()).decode()


def verify_password(plain: str, hashed: str) -> bool:
    return bcrypt.checkpw(plain.encode(), hashed.encode())


def create_token(login: str, role: str) -> str:
    now = datetime.utcnow()
    payload = {
        "sub": login,
        "role": role,
        "iat": now,
        "exp": now + timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES),
    }
    return jwt.encode(payload, SECRET_KEY, algorithm=ALGORITHM)


def decode_token(token: str) -> dict:
    try:
        return jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
    except jwt.ExpiredSignatureError:
        raise HTTPException(status_code=401, detail={"errorMessage": "Token expired", "errorCode": 40101})
    except Exception:
        raise HTTPException(status_code=401, detail={"errorMessage": "Invalid token", "errorCode": 40101})


def get_current_user(credentials: HTTPAuthorizationCredentials) -> dict:
    return decode_token(credentials.credentials)


def require_admin(credentials: HTTPAuthorizationCredentials) -> dict:
    user = get_current_user(credentials)
    if user.get("role") != "ADMIN":
        raise HTTPException(status_code=403, detail={"errorMessage": "Admin role required", "errorCode": 40301})
    return user