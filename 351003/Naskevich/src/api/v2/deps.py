from dataclasses import dataclass
from typing import Annotated

import jwt
from fastapi import Depends, HTTPException
from fastapi.security import HTTPAuthorizationCredentials, HTTPBearer

from src.api.dependencies import SessionDep
from src.database.repositories.editor import EditorRepository
from src.models.user_role import UserRole
from src.security.jwt_tokens import decode_token

_bearer = HTTPBearer()
_bearer_optional = HTTPBearer(auto_error=False)


@dataclass(frozen=True, slots=True)
class CurrentUser:
    editor_id: int
    login: str
    role: UserRole


async def get_current_user(
    credentials: Annotated[HTTPAuthorizationCredentials, Depends(_bearer)],
    session: SessionDep,
) -> CurrentUser:
    try:
        payload = decode_token(credentials.credentials)
    except jwt.PyJWTError as e:
        raise HTTPException(status_code=401, detail="Invalid or expired token") from e
    login = payload.get("sub")
    if not login or not isinstance(login, str):
        raise HTTPException(status_code=401, detail="Invalid token")
    repo = EditorRepository(session)
    editor = await repo.get_by_login(login)
    if editor is None:
        raise HTTPException(status_code=401, detail="User not found")
    return CurrentUser(editor_id=editor.id, login=editor.login, role=editor.role)


async def get_optional_user(
    credentials: Annotated[
        HTTPAuthorizationCredentials | None,
        Depends(_bearer_optional),
    ],
    session: SessionDep,
) -> CurrentUser | None:
    if credentials is None:
        return None
    try:
        payload = decode_token(credentials.credentials)
    except jwt.PyJWTError:
        raise HTTPException(status_code=401, detail="Invalid or expired token")
    login = payload.get("sub")
    if not login or not isinstance(login, str):
        raise HTTPException(status_code=401, detail="Invalid token")
    editor = await EditorRepository(session).get_by_login(login)
    if editor is None:
        raise HTTPException(status_code=401, detail="User not found")
    return CurrentUser(editor_id=editor.id, login=editor.login, role=editor.role)


CurrentUserDep = Annotated[CurrentUser, Depends(get_current_user)]
OptionalUserDep = Annotated[CurrentUser | None, Depends(get_optional_user)]


def ensure_admin(user: CurrentUser) -> None:
    if user.role != UserRole.ADMIN:
        raise HTTPException(status_code=403, detail="Admin role required")


def ensure_editor_mutate_self_or_admin(user: CurrentUser, editor_id: int) -> None:
    if user.role == UserRole.ADMIN:
        return
    if user.editor_id != editor_id:
        raise HTTPException(status_code=403, detail="Forbidden")


def ensure_tweet_write(user: CurrentUser, editor_id: int) -> None:
    if user.role == UserRole.ADMIN:
        return
    if user.editor_id != editor_id:
        raise HTTPException(status_code=403, detail="Forbidden")
