from fastapi import HTTPException
from sqlalchemy.ext.asyncio import AsyncSession

from publisher.app.infrastructure.db.repo import WriterRepo
from publisher.app.core.security.password import hash_password, verify_password
from publisher.app.core.security.jwt import create_access_token


class AuthService:
    def __init__(self):
        self.writer_repo = WriterRepo()

    async def register_writer(self, session: AsyncSession, dto):
        existing = await self.writer_repo.get_by_login(session, dto.login)
        if existing:
            raise HTTPException(status_code=409, detail="Login already exists")

        data = {
            "login": dto.login,
            "password": hash_password(dto.password),
            "firstname": dto.first_name,
            "lastname": dto.last_name,
            "role": dto.role,
        }

        return await self.writer_repo.create(session, data)

    async def login(self, session: AsyncSession, dto):
        writer = await self.writer_repo.get_by_login(session, dto.login)
        if not writer:
            raise HTTPException(status_code=401, detail="Invalid login or password")

        if not verify_password(dto.password, writer.password):
            raise HTTPException(status_code=401, detail="Invalid login or password")

        token = create_access_token(login=writer.login, role=writer.role)

        return {
            "access_token": token,
            "token_type": "Bearer",
        }