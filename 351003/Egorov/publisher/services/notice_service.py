from typing import List, Optional

from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from publisher.models.notice import Notice
from publisher.models.story import Story
from publisher.repositories.base import AbstractRepository
from publisher.schemas.common import Page, PaginationParams
from publisher.schemas.notice import NoticeCreate, NoticeRead, NoticeUpdate


class NoticeService:
    def __init__(
        self,
        notice_repo: AbstractRepository[Notice],
        session: AsyncSession,
    ) -> None:
        self._notice_repo = notice_repo
        self._session = session

    async def _ensure_story_exists(self, story_id: int) -> None:
        res = await self._session.execute(select(Story.id).where(Story.id == story_id))
        if res.scalar_one_or_none() is None:
            raise ValueError(f"Story with id {story_id} not found")

    async def create(self, dto: NoticeCreate) -> NoticeRead:
        await self._ensure_story_exists(dto.story_id)
        entity = Notice(content=dto.content, story_id=dto.story_id)
        created = await self._notice_repo.create(entity)
        await self._session.commit()
        await self._session.refresh(created)
        return NoticeRead.model_validate(created)

    async def get(self, notice_id: int) -> Optional[NoticeRead]:
        entity = await self._notice_repo.get_by_id(notice_id)
        if not entity:
            return None
        return NoticeRead.model_validate(entity)

    async def get_all(self, pagination: PaginationParams) -> Page[NoticeRead]:
        page = await self._notice_repo.get_all(pagination)
        return Page[NoticeRead](
            items=[NoticeRead.model_validate(i) for i in page.items],
            total=page.total,
            page=page.page,
            size=page.size,
        )

    async def update(self, notice_id: int, dto: NoticeUpdate) -> Optional[NoticeRead]:
        existing = await self._notice_repo.get_by_id(notice_id)
        if not existing:
            return None
        await self._ensure_story_exists(dto.story_id)
        existing.content = dto.content
        existing.story_id = dto.story_id
        updated = await self._notice_repo.update(notice_id, existing)
        await self._session.commit()
        if not updated:
            return None
        await self._session.refresh(updated)
        return NoticeRead.model_validate(updated)

    async def delete(self, notice_id: int) -> bool:
        deleted = await self._notice_repo.delete(notice_id)
        if deleted:
            await self._session.commit()
        return deleted

    async def get_by_story(self, story_id: int, pagination: PaginationParams) -> List[NoticeRead]:
        await self._ensure_story_exists(story_id)
        # Filtering by story_id, but we don't need total/paging here in tests; still reuse repo
        page = await self._notice_repo.get_all(pagination, filters={"story_id": story_id})
        return [NoticeRead.model_validate(i) for i in page.items]

