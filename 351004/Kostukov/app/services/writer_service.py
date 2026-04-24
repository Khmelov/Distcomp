from sqlalchemy.ext.asyncio import AsyncSession
from fastapi import HTTPException
from app.core.writers.dto import WriterRequestTo
from app.infrastructure.db.repo import WriterRepo
from sqlalchemy.exc import IntegrityError

class WriterService:
    def __init__(self):
        self.repo = WriterRepo()

    async def create(self, db: AsyncSession, dto: WriterRequestTo):
        try:
            return await self.repo.create(db, dto.model_dump(exclude_none=True))
        except IntegrityError:
            await db.rollback()
            raise HTTPException(status_code=403, detail="Writer already exists")

    async def get_all(self, db: AsyncSession, skip = 0, limit = 100, sort = "id", name = None):
        return await self.repo.get_all(db, skip = skip, limit = limit, sort_by = sort, firstname = name)

    async def get_by_id(self, db: AsyncSession, id : int):
        return await self.repo.get_by_id(db, id)

    async def update(self, db: AsyncSession, id: int, dto: WriterRequestTo):
        return await self.repo.update(db, id, dto.model_dump(exclude_none=True))

    async def delete(self, db: AsyncSession, id: int):
        return await self.repo.delete(db, id)