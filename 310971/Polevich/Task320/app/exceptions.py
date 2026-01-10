from __future__ import annotations

from dataclasses import dataclass


@dataclass
class AppError(Exception):
    status_code: int
    error_message: str
    error_code: str

    def __str__(self) -> str:
        return f"{self.status_code} {self.error_code}: {self.error_message}"


def error_code(status: int, suffix: int) -> str:
    """Generate five-digit error code: first three digits = HTTP status."""
    return f"{status}{suffix:02d}"
