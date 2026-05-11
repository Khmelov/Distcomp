from typing import List

from sqlalchemy.ext.asyncio import AsyncSession

from src.domain.repositories import WriterRepository
from src.schemas.writer import WriterResponseTo, WriterRequestTo
from .auth import AuthService


class WriterService:
    def __init__(self, session: AsyncSession) -> None:
        self.session = session
        self.writer_repo = WriterRepository(session)

    async def get_one(self, writer_id: int) -> WriterResponseTo:
        writer = await self.writer_repo.get_one(writer_id)
        return WriterResponseTo.model_validate(writer)

    async def get_all(self) -> List[WriterResponseTo]:
        writers = await self.writer_repo.get_all()
        return [WriterResponseTo.model_validate(a) for a in writers]

    async def create(self, dto: WriterRequestTo) -> WriterResponseTo:
        dto.password = AuthService.get_hashed_password(dto.password)
        user_args = dto.model_dump()
        created_writer = await self.writer_repo.create(**user_args)
        await self.session.commit()
        return WriterResponseTo.model_validate(created_writer)

    async def update(self, writer_id: int, dto: WriterRequestTo) -> WriterResponseTo:
        user_args = dto.model_dump()
        updated_writer = await self.writer_repo.update(writer_id, **user_args)
        await self.session.commit()
        return WriterResponseTo.model_validate(updated_writer)

    async def delete(self, writer_id: int) -> None:
        await self.writer_repo.delete(writer_id)
        await self.session.commit()