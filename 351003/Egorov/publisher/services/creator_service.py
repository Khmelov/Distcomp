from typing import Optional

from publisher.models.creator import Creator
from publisher.repositories.base import AbstractRepository
from publisher.schemas.common import Page, PaginationParams
from publisher.schemas.creator import CreatorCreate, CreatorRead, CreatorUpdate


class CreatorService:
    def __init__(self, repo: AbstractRepository[Creator]) -> None:
        self._repo = repo

    async def create(self, dto: CreatorCreate) -> CreatorRead:
        entity = Creator(login=dto.login, name=dto.name, email=dto.email)
        created = await self._repo.create(entity)
        return CreatorRead.model_validate(created)

    async def get(self, creator_id: int) -> Optional[CreatorRead]:
        entity = await self._repo.get_by_id(creator_id)
        if not entity:
            return None
        return CreatorRead.model_validate(entity)

    async def get_all(self, pagination: PaginationParams) -> Page[CreatorRead]:
        page = await self._repo.get_all(pagination)
        return Page[CreatorRead](
            items=[CreatorRead.model_validate(i) for i in page.items],
            total=page.total,
            page=page.page,
            size=page.size,
        )

    async def update(self, creator_id: int, dto: CreatorUpdate) -> Optional[CreatorRead]:
        existing = await self._repo.get_by_id(creator_id)
        if not existing:
            return None
        updated_entity = Creator(
            id=creator_id,
            login=dto.login,
            name=dto.name,
            email=dto.email,
            created_at=existing.created_at,
        )
        updated = await self._repo.update(creator_id, updated_entity)
        if not updated:
            return None
        return CreatorRead.model_validate(updated)

    async def delete(self, creator_id: int) -> bool:
        return await self._repo.delete(creator_id)

