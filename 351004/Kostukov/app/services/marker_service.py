from sqlalchemy.ext.asyncio import AsyncSession
from app.core.markers.dto import MarkerRequestTo
from app.infrastructure.db.repo import MarkerRepo

class MarkerService:
    def __init__(self):
        self.repo = MarkerRepo()

    async def create(self, db: AsyncSession, dto: MarkerRequestTo):
        return await self.repo.create(db, dto.model_dump(exclude_none=True))

    async def get_all(self, db: AsyncSession, skip = 0, limit = 100, sort = "id", name = None):
        return await self.repo.get_all(db, skip = skip, limit = limit, sort_by = sort, first_name = name)

    async def get_by_id(self, db: AsyncSession, id : int):
        return await self.repo.get_by_id(db, id)

    async def update(self, db: AsyncSession, id: int, dto: MarkerRequestTo):
        return await self.repo.update(db, id, dto.model_dump(exclude_none=True))

    async def delete(self, db: AsyncSession, id: int):
        return await self.repo.delete(db, id)