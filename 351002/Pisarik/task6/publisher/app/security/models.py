from __future__ import annotations

from dataclasses import dataclass


@dataclass(frozen=True)
class TokenUser:
    login: str
    author_id: int
    role: str
