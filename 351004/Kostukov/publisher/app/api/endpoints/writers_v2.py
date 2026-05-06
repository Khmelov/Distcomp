from fastapi import APIRouter, Depends, status
from sqlalchemy.ext.asyncio import AsyncSession

from publisher.app.infrastructure.db.session import get_session
from publisher.app.core.writers.dto_v2 import (
    WriterRegisterRequestTo,
    WriterLoginRequestTo,
    WriterResponseTo,
)
from publisher.app.services.auth_service import AuthService

router = APIRouter(prefix="/api/v2.0", tags=["writers-v2"])
auth_service = AuthService()


@router.post("/writers", response_model=WriterResponseTo, status_code=status.HTTP_201_CREATED)
async def register_writer(
    dto: WriterRegisterRequestTo,
    session: AsyncSession = Depends(get_session),
):
    writer = await auth_service.register_writer(session, dto)
    return WriterResponseTo(
        id=writer.id,
        login=writer.login,
        firstName=writer.firstname,
        lastName=writer.lastname,
        role=writer.role,
        createdAt=writer.created_at,
    )


@router.post("/login")
async def login_writer(
    dto: WriterLoginRequestTo,
    session: AsyncSession = Depends(get_session),
):
    return await auth_service.login(session, dto)