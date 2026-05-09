from datetime import datetime
from typing import List, Optional

from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from publisher.models.creator import Creator
from publisher.models.marker import Marker
from publisher.models.story import Story
from publisher.repositories.base import AbstractRepository
from publisher.schemas.common import Page, PaginationParams
from publisher.schemas.story import StoryCreate, StoryRead, StoryUpdate


class StoryService:
    def __init__(
        self,
        story_repo: AbstractRepository[Story],
        session: AsyncSession,
    ) -> None:
        self._story_repo = story_repo
        self._session = session

    async def _ensure_creator_exists(self, creator_id: int) -> None:
        res = await self._session.execute(select(Creator).where(Creator.id == creator_id))
        creator = res.scalar_one_or_none()
        if not creator:
            raise ValueError(f"Creator with id {creator_id} not found")

    async def _ensure_markers_exist(self, marker_ids: List[int]) -> None:
        if not marker_ids:
            return
        res = await self._session.execute(select(Marker.id).where(Marker.id.in_(marker_ids)))
        existing_ids = {row[0] for row in res.all()}
        missing = set(marker_ids) - existing_ids
        if missing:
            raise ValueError(f"Markers not found: {sorted(missing)}")

    async def create(self, dto: StoryCreate) -> StoryRead:
        await self._ensure_creator_exists(dto.creator_id)
        await self._ensure_markers_exist(dto.marker_ids)

        now = datetime.utcnow()
        story = Story(
            title=dto.title,
            content=dto.content,
            creator_id=dto.creator_id,
            created_at=now,
            updated_at=now,
        )
        for marker_id in dto.marker_ids:
            marker = await self._session.get(Marker, marker_id)
            if marker:
                story.markers.append(marker)

        created = await self._story_repo.create(story)
        await self._session.commit()
        # refresh to load markers
        await self._session.refresh(created)
        marker_ids = [m.id for m in created.markers]
        return StoryRead(
            id=created.id,
            title=created.title,
            content=created.content,
            creator_id=created.creator_id,
            marker_ids=marker_ids,
            created_at=created.created_at,
            updated_at=created.updated_at,
        )

    async def get(self, story_id: int) -> Optional[StoryRead]:
        story = await self._story_repo.get_by_id(story_id)
        if not story:
            return None
        marker_ids = [m.id for m in story.markers]
        return StoryRead(
            id=story.id,
            title=story.title,
            content=story.content,
            creator_id=story.creator_id,
            marker_ids=marker_ids,
            created_at=story.created_at,
            updated_at=story.updated_at,
        )

    async def get_all(self, pagination: PaginationParams) -> Page[StoryRead]:
        page = await self._story_repo.get_all(pagination)
        items = []
        for story in page.items:
            marker_ids = [m.id for m in story.markers]
            items.append(
                StoryRead(
                    id=story.id,
                    title=story.title,
                    content=story.content,
                    creator_id=story.creator_id,
                    marker_ids=marker_ids,
                    created_at=story.created_at,
                    updated_at=story.updated_at,
                )
            )
        return Page[StoryRead](items=items, total=page.total, page=page.page, size=page.size)

    async def update(self, story_id: int, dto: StoryUpdate) -> Optional[StoryRead]:
        existing = await self._story_repo.get_by_id(story_id)
        if not existing:
            return None

        await self._ensure_creator_exists(dto.creator_id)
        await self._ensure_markers_exist(dto.marker_ids)

        existing.title = dto.title
        existing.content = dto.content
        existing.creator_id = dto.creator_id
        existing.updated_at = datetime.utcnow()

        # update markers
        existing.markers.clear()
        for marker_id in dto.marker_ids:
            marker = await self._session.get(Marker, marker_id)
            if marker:
                existing.markers.append(marker)

        updated = await self._story_repo.update(story_id, existing)
        await self._session.commit()
        if not updated:
            return None
        await self._session.refresh(updated)
        marker_ids = [m.id for m in updated.markers]
        return StoryRead(
            id=updated.id,
            title=updated.title,
            content=updated.content,
            creator_id=updated.creator_id,
            marker_ids=marker_ids,
            created_at=updated.created_at,
            updated_at=updated.updated_at,
        )

    async def delete(self, story_id: int) -> bool:
        deleted = await self._story_repo.delete(story_id)
        if deleted:
            await self._session.commit()
        return deleted

