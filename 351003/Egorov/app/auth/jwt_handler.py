import os
from datetime import datetime, timedelta, timezone

from jose import JWTError, jwt
from dotenv import load_dotenv

from app.models.creator import CreatorRole


ALGORITHM = "HS256"
ACCESS_TOKEN_EXPIRE_MINUTES = 60
load_dotenv()


def _jwt_secret_key() -> str:
    secret_key = os.getenv("JWT_SECRET_KEY")
    if not secret_key:
        raise ValueError("JWT_SECRET_KEY environment variable is required")
    return secret_key


def create_access_token(login: str, role: CreatorRole) -> str:
    now = datetime.now(timezone.utc)
    payload = {
        "sub": login,
        "iat": int(now.timestamp()),
        "exp": int((now + timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)).timestamp()),
        "role": role.value,
    }
    return jwt.encode(payload, _jwt_secret_key(), algorithm=ALGORITHM)


def decode_access_token(token: str) -> dict:
    try:
        return jwt.decode(token, _jwt_secret_key(), algorithms=[ALGORITHM])
    except JWTError as exc:
        raise ValueError("Invalid or expired token") from exc
