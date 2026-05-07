from typing import Optional

from publisher.models.marker import Marker
from publisher.repositories.base import AbstractRepository
from publisher.schemas.common import Page, PaginationParams
from publisher.schemas.marker import MarkerCreate, MarkerRead, MarkerUpdate


class MarkerService:
    def __init__(self, repo: AbstractRepository[Marker]) -> None:
        self._repo = repo

    async def create(self, dto: MarkerCreate) -> MarkerRead:
        entity = Marker(name=dto.name)
        created = await self._repo.create(entity)
        return MarkerRead.model_validate(created)

    async def get(self, marker_id: int) -> Optional[MarkerRead]:
        entity = await self._repo.get_by_id(marker_id)
        if not entity:
            return None
        return MarkerRead.model_validate(entity)

    async def get_all(self, pagination: PaginationParams) -> Page[MarkerRead]:
        page = await self._repo.get_all(pagination)
        return Page[MarkerRead](
            items=[MarkerRead.model_validate(i) for i in page.items],
            total=page.total,
            page=page.page,
            size=page.size,
        )

    async def update(self, marker_id: int, dto: MarkerUpdate) -> Optional[MarkerRead]:
        existing = await self._repo.get_by_id(marker_id)
        if not existing:
            return None
        updated_entity = Marker(
            id=marker_id,
            name=dto.name,
            created_at=existing.created_at,
        )
        updated = await self._repo.update(marker_id, updated_entity)
        if not updated:
            return None
        return MarkerRead.model_validate(updated)

    async def delete(self, marker_id: int) -> bool:
        return await self._repo.delete(marker_id)

