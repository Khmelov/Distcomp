from __future__ import annotations

from fastapi import HTTPException, status

from app.security.models import TokenUser


def require_admin(user: TokenUser) -> None:
    if user.role != "ADMIN":
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail={"errorMessage": "Administrator role required", "errorCode": 40301},
        )


def require_customer_self_author(user: TokenUser, author_id: int) -> None:
    if user.role == "ADMIN":
        return
    if user.role == "CUSTOMER" and user.author_id == author_id:
        return
    raise HTTPException(
        status_code=status.HTTP_403_FORBIDDEN,
        detail={"errorMessage": "You can modify only your own author profile", "errorCode": 40302},
    )


def require_customer_own_news_author(user: TokenUser, news_author_id: int) -> None:
    if user.role == "ADMIN":
        return
    if user.role == "CUSTOMER" and user.author_id == news_author_id:
        return
    raise HTTPException(
        status_code=status.HTTP_403_FORBIDDEN,
        detail={"errorMessage": "You can modify only news owned by you", "errorCode": 40303},
    )


def require_admin_for_marks_write(user: TokenUser) -> None:
    if user.role != "ADMIN":
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail={"errorMessage": "Marks are read-only for this role", "errorCode": 40304},
        )
