from datetime import datetime
from typing import List

from app.dtos.story_request import StoryRequestTo
from app.dtos.story_response import StoryResponseTo
from app.models.creator import Creator
from app.models.marker import Marker
from app.models.story import Story
from app.repositories.base_repository import BaseRepository


class StoryService:
    def __init__(
        self,
        story_repository: BaseRepository[Story],
        creator_repository: BaseRepository[Creator],
        marker_repository: BaseRepository[Marker],
    ) -> None:
        self._story_repository = story_repository
        self._creator_repository = creator_repository
        self._marker_repository = marker_repository

    def create_story(self, dto: StoryRequestTo) -> StoryResponseTo:
        self._ensure_creator_exists(dto.creator_id)
        marker_ids = dto.marker_ids or []
        self._ensure_markers_exist(marker_ids)

        now = datetime.utcnow()
        entity = Story(
            title=dto.title,
            content=dto.content,
            creator_id=dto.creator_id,
            marker_ids=marker_ids,
            created_at=now,
            updated_at=now,
        )
        created = self._story_repository.create(entity)
        return self._to_response(created)

    def get_story(self, story_id: int) -> StoryResponseTo | None:
        entity = self._story_repository.read_by_id(story_id)
        return self._to_response(entity) if entity else None

    def get_all_stories(self) -> List[StoryResponseTo]:
        return [self._to_response(s) for s in self._story_repository.read_all()]

    def update_story(self, story_id: int, dto: StoryRequestTo) -> StoryResponseTo | None:
        existing = self._story_repository.read_by_id(story_id)
        if existing is None:
            return None

        self._ensure_creator_exists(dto.creator_id)
        marker_ids = dto.marker_ids or []
        self._ensure_markers_exist(marker_ids)

        now = datetime.utcnow()
        entity = Story(
            id=story_id,
            title=dto.title,
            content=dto.content,
            creator_id=dto.creator_id,
            marker_ids=marker_ids,
            created_at=existing.created_at or now,
            updated_at=now,
        )
        updated = self._story_repository.update(story_id, entity)
        return self._to_response(updated)

    def delete_story(self, story_id: int) -> bool:
        if self._story_repository.read_by_id(story_id) is None:
            return False
        self._story_repository.delete(story_id)
        return True

    def _ensure_creator_exists(self, creator_id: int) -> None:
        creator = self._creator_repository.read_by_id(creator_id)
        if creator is None:
            raise ValueError(f"Creator with id {creator_id} does not exist")

    def _ensure_markers_exist(self, marker_ids: List[int]) -> None:
        for marker_id in marker_ids:
            if self._marker_repository.read_by_id(marker_id) is None:
                raise ValueError(f"Marker with id {marker_id} does not exist")

    @staticmethod
    def _to_response(entity: Story) -> StoryResponseTo:
        return StoryResponseTo(
            id=entity.id or 0,
            title=entity.title,
            content=entity.content,
            creator_id=entity.creator_id or 0,
            marker_ids=list(entity.marker_ids),
            created_at=entity.created_at,
            updated_at=entity.updated_at,
            links={"self": f"/api/v1.0/stories/{entity.id}"},
        )

