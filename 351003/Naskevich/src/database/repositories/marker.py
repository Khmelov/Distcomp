from sqlalchemy import select, delete

from src.database.repositories.base import SQLAlchemyRepository
from src.database.tables import markers_table
from src.models.marker import Marker
from src.repositories.marker import AbstractMarkerRepository


class MarkerRepository(SQLAlchemyRepository, AbstractMarkerRepository):

    async def get_by_id(self, entity_id: int) -> Marker | None:
        result = await self._session.execute(
            select(Marker).where(markers_table.c.id == entity_id)
        )
        return result.scalar_one_or_none()

    async def get_all(self) -> list[Marker]:
        result = await self._session.execute(select(Marker))
        return list(result.scalars().all())

    async def create(self, entity: Marker) -> Marker:
        self._session.add(entity)
        await self._session.flush()
        await self._session.refresh(entity)
        return entity

    async def update(self, entity: Marker) -> Marker | None:
        existing = await self.get_by_id(entity.id)
        if existing is None:
            return None
        existing.name = entity.name
        await self._session.flush()
        await self._session.refresh(existing)
        return existing

    async def delete(self, entity_id: int) -> bool:
        result = await self._session.execute(
            delete(Marker).where(markers_table.c.id == entity_id)
        )
        return result.rowcount > 0

    async def get_by_name(self, name: str) -> Marker | None:
        result = await self._session.execute(
            select(Marker).where(markers_table.c.name == name)
        )
        return result.scalar_one_or_none()
