from __future__ import annotations

from dataclasses import dataclass
from typing import Any, Dict, Optional


@dataclass
class AppError(Exception):
    status_code: int
    error_message: str
    error_code: str
    details: Optional[Dict[str, Any]] = None

    def __str__(self) -> str:
        return f"{self.status_code} {self.error_code}: {self.error_message}"


def make_error_code(status: int, suffix: int) -> str:
    """Build a five digit error code where the first three digits match HTTP status."""
    return f"{status}{suffix:02d}"
