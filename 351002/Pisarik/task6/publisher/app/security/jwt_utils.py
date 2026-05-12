from __future__ import annotations

import os
import time
from typing import Any, Dict

import jwt

_ALGO = "HS256"


def _secret() -> str:
    return os.getenv("JWT_SECRET_KEY", "dev-secret-change-in-production-min-32-chars-long-key!!")


def create_access_token(*, sub: str, role: str, ttl_sec: int | None = None) -> str:
    if ttl_sec is None:
        ttl_sec = int(os.getenv("JWT_EXPIRE_SECONDS", "86400"))
    now = int(time.time())
    payload: Dict[str, Any] = {"sub": sub, "role": role, "iat": now, "exp": now + ttl_sec}
    return jwt.encode(payload, _secret(), algorithm=_ALGO)


def decode_access_token(token: str) -> Dict[str, Any]:
    return jwt.decode(token, _secret(), algorithms=[_ALGO])
