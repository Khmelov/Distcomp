from fastapi import Depends
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from sqlalchemy.ext.asyncio import AsyncSession

from src.core import get_db
from src.core.errors import UnauthorizedError, ForbiddenError
from src.domain.models.models import UserRole
from src.services import AuthService

security = HTTPBearer()

def get_auth_service(
    session: AsyncSession = Depends(get_db)
) -> AuthService:
    return AuthService(session)

async def get_active_user_login(
    authorization: HTTPAuthorizationCredentials = Depends(security),
    auth_service: AuthService = Depends(get_auth_service)
) -> str:
    user_info = await auth_service.validate_token(authorization.credentials)
    if not user_info:
        raise UnauthorizedError("Unauthorized user!")

    if user_info.role != UserRole.ADMIN:
        raise ForbiddenError("Forbidden for user with current role!")

    return user_info.login