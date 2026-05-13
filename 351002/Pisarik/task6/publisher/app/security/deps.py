from __future__ import annotations

from typing import Annotated, Optional, Union

from fastapi import Depends, HTTPException, status
from fastapi.security import HTTPAuthorizationCredentials, HTTPBearer
from sqlalchemy import select
from sqlalchemy.orm import Session

from app.db.database import get_db
from app.db.orm import AuthorOrm
from app.security.jwt_utils import decode_access_token
from app.security.models import TokenUser

_bearer = HTTPBearer(auto_error=False)


class NonNumericAuthorDeletePath:
    """Returned by delete dependency when path id is not an integer (no Bearer required)."""


NON_NUMERIC_AUTHOR_DELETE_PATH = NonNumericAuthorDeletePath()


def _user_from_bearer(
    creds: Optional[HTTPAuthorizationCredentials],
    db: Session,
) -> TokenUser:
    if creds is None or (creds.scheme or "").lower() != "bearer" or not creds.credentials:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail={"errorMessage": "Missing or invalid Authorization header", "errorCode": 40101},
        )
    try:
        payload = decode_access_token(creds.credentials)
    except Exception as e:  # noqa: BLE001
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail={"errorMessage": "Invalid or expired token", "errorCode": 40102},
        ) from e
    login = payload.get("sub")
    if not login or not isinstance(login, str):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail={"errorMessage": "Invalid token payload", "errorCode": 40103},
        )
    row = db.execute(select(AuthorOrm).where(AuthorOrm.login == login)).scalar_one_or_none()
    if row is None:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail={"errorMessage": "User no longer exists", "errorCode": 40104},
        )
    return TokenUser(login=row.login, author_id=int(row.id), role=str(row.role))


def get_current_user(
    creds: Annotated[Optional[HTTPAuthorizationCredentials], Depends(_bearer)],
    db: Session = Depends(get_db),
) -> TokenUser:
    return _user_from_bearer(creds, db)


def get_current_user_for_v2_author_delete(
    author_id: str,
    creds: Annotated[Optional[HTTPAuthorizationCredentials], Depends(_bearer)],
    db: Session = Depends(get_db),
) -> Union[TokenUser, NonNumericAuthorDeletePath]:
    try:
        int(author_id)
    except ValueError:
        return NON_NUMERIC_AUTHOR_DELETE_PATH
    return _user_from_bearer(creds, db)
