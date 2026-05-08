"""JWT generation and validation helpers.

Tokens contain the required claims (sub, iat, exp, role) and are signed with
HS256 using the ``JWT_SECRET`` from Django settings.
"""
import time

import jwt
from django.conf import settings


class JWTError(Exception):
    """Raised for any JWT validation failure (invalid signature, expired ...)."""


def _now() -> int:
    return int(time.time())


def generate_token(login: str, role: str, lifetime_seconds: int | None = None) -> dict:
    iat = _now()
    exp = iat + (lifetime_seconds or settings.JWT_ACCESS_TOKEN_LIFETIME_SECONDS)
    payload = {
        "sub": login,
        "role": role,
        "iat": iat,
        "exp": exp,
        "iss": settings.JWT_ISSUER,
    }
    token = jwt.encode(payload, settings.JWT_SECRET, algorithm=settings.JWT_ALGORITHM)
    if isinstance(token, bytes):
        token = token.decode("utf-8")
    return {
        "access_token": token,
        "token_type": "Bearer",
        "expires_in": exp - iat,
        "issued_at": iat,
        "role": role,
    }


def decode_token(token: str) -> dict:
    try:
        payload = jwt.decode(
            token,
            settings.JWT_SECRET,
            algorithms=[settings.JWT_ALGORITHM],
            options={"require": ["sub", "iat", "exp", "role"]},
        )
    except jwt.ExpiredSignatureError as exc:
        raise JWTError("Token has expired") from exc
    except jwt.InvalidTokenError as exc:
        raise JWTError(f"Invalid token: {exc}") from exc
    return payload
