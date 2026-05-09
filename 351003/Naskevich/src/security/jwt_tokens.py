import time
from typing import Any

import jwt

from src.config import JwtConfig


def create_access_token(*, sub: str, role: str, cfg: JwtConfig | None = None) -> str:
    c = cfg or JwtConfig()
    now = int(time.time())
    payload: dict[str, Any] = {
        "sub": sub,
        "role": role,
        "iat": now,
        "exp": now + c.access_token_expire_seconds,
    }
    return jwt.encode(payload, c.secret.get_secret_value(), algorithm=c.algorithm)


def decode_token(token: str, cfg: JwtConfig | None = None) -> dict[str, Any]:
    c = cfg or JwtConfig()
    return jwt.decode(
        token,
        c.secret.get_secret_value(),
        algorithms=[c.algorithm],
    )
