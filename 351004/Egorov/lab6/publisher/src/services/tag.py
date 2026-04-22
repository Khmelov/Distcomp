from typing import List

from sqlalchemy.ext.asyncio import AsyncSession

from src.domain.repositories import TagRepository
from src.schemas.tag import TagResponseTo, TagRequestTo


class TagService:
    def __init__(self, session: AsyncSession) -> None:
        self.session = session
        self.tag_repo = TagRepository(session)

    async def get_one(self, tag_id: int) -> TagResponseTo:
        tag = await self.tag_repo.get_one(tag_id)
        return TagResponseTo.model_validate(tag)

    async def get_all(self) -> List[TagResponseTo]:
        tags = await self.tag_repo.get_all()
        return [TagResponseTo.model_validate(t) for t in tags]

    async def create(self, dto: TagRequestTo) -> TagResponseTo:
        tag_args = dto.model_dump()
        created_tag = await self.tag_repo.create(**tag_args)
        await self.session.commit()
        return TagResponseTo.model_validate(created_tag)

    async def update(self, tag_id: int, dto: TagRequestTo) -> TagResponseTo:
        tag_args = dto.model_dump()
        updated_tag = await self.tag_repo.update(tag_id, **tag_args)
        await self.session.commit()
        return TagResponseTo.model_validate(updated_tag)

    async def delete(self, tag_id: int) -> None:
        await self.tag_repo.delete(tag_id)
        await self.session.commit()
