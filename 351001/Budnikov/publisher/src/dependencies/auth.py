from fastapi import Depends
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
import jwt
from typing import Annotated

from src.core.exceptions import BaseAppException
from src.core.security import SECRET_KEY, ALGORITHM
from src.models.editor import Editor


security = HTTPBearer()


async def get_current_user(credentials: HTTPAuthorizationCredentials = Depends(security)) -> Editor:
    token = credentials.credentials
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        login: str = payload.get("sub")
        if login is None:
            raise BaseAppException(401, "40101", "Invalid authentication credentials")
    except jwt.PyJWTError:
        raise BaseAppException(401, "40101", "Token is invalid or expired")

    user = await Editor.get_or_none(login=login)
    if user is None:
        raise BaseAppException(401, "40102", "User not found")

    return user


def verify_permissions(current_user: Editor, owner_id: int = None):
    if current_user.role == "ADMIN":
        return True

    if current_user.role == "CUSTOMER" and owner_id and current_user.id == owner_id:
        return True

    raise BaseAppException(403, "40301", "Forbidden: You don't have enough permissions")


CurrentUserDep = Annotated[Editor, Depends(get_current_user)]