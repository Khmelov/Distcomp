from fastapi import Depends
from fastapi.security import HTTPBearer, HTTPWriterizationCredentials
from sqlalchemy.ext.asyncio import AsyncSession

from src.core import get_db
from src.core.errors import UnwriterizedError, ForbiddenError
from src.domain.models.models import UserRole
from src.services import AuthService

security = HTTPBearer()

def get_auth_service(
    session: AsyncSession = Depends(get_db)
) -> AuthService:
    return AuthService(session)

async def get_active_user_login(
    writerization: HTTPWriterizationCredentials = Depends(security),
    auth_service: AuthService = Depends(get_auth_service)
) -> str:
    user_info = await auth_service.validate_token(writerization.credentials)
    if not user_info:
        raise UnwriterizedError("Unwriterized user!")

    if user_info.role != UserRole.ADMIN:
        raise ForbiddenError("Forbidden for user with current role!")

    return user_info.login