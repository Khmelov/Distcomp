from fastapi import Depends, HTTPException, status
from fastapi.security import HTTPAuthorizationCredentials, HTTPBearer

from app.auth.jwt_handler import decode_access_token
from app.models.creator import Creator, CreatorRole


bearer_scheme = HTTPBearer(auto_error=False)


def _get_creator_by_login(login: str) -> Creator | None:
    from main import creator_repository

    creators = creator_repository.read_all()
    for creator in creators:
        if creator.login == login:
            return creator
    return None


def get_current_user(credentials: HTTPAuthorizationCredentials | None = Depends(bearer_scheme)) -> Creator:
    if credentials is None or credentials.scheme.lower() != "bearer":
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid authentication credentials",
        )

    try:
        payload = decode_access_token(credentials.credentials)
    except ValueError as exc:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid authentication credentials",
        ) from exc

    login = payload.get("sub")
    if not login:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid authentication credentials",
        )

    user = _get_creator_by_login(login)
    if not user:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid authentication credentials",
        )
    return user


def require_admin(current_user: Creator = Depends(get_current_user)) -> Creator:
    if current_user.role != CreatorRole.ADMIN:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Forbidden")
    return current_user


def require_customer(current_user: Creator = Depends(get_current_user)) -> Creator:
    if current_user.role != CreatorRole.CUSTOMER:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Forbidden")
    return current_user


def require_customer_or_admin(current_user: Creator = Depends(get_current_user)) -> Creator:
    if current_user.role not in {CreatorRole.ADMIN, CreatorRole.CUSTOMER}:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Forbidden")
    return current_user
