from __future__ import annotations

from app.exceptions import AppError, error_code


def check_length(value: str, min_len: int, max_len: int, field: str) -> None:
    if value is None:
        raise AppError(400, f"{field} is required", error_code(400, 1))
    if not (min_len <= len(value) <= max_len):
        raise AppError(400, f"{field} length must be between {min_len} and {max_len}", error_code(400, 1))
