from typing import List, Optional
from app.dtos.notice_request import NoticeRequestTo
from app.dtos.notice_response import NoticeResponseTo
from app.models.notice import Notice
from app.repositories.notice_repository import NoticeRepository
from discussion.kafka import use


class NoticeService:
    def __init__(self, notice_repository: NoticeRepository):
        self._notice_repository = notice_repository
        self._current_id = 1

    def _generate_id(self) -> int:
        # Простая генерация ID (в Cassandra нет автоинкремента)
        current = self._current_id
        self._current_id += 1
        return current

    def create_notice(self, dto: NoticeRequestTo) -> NoticeResponseTo | None:
        if len(dto.content) < 2 or len(dto.content) > 2048:
            raise ValueError("Content must be between 2 and 2048 characters")

        # Создание entity
        entity = Notice(
            id=self._generate_id(),
            content=dto.content,
            story_id=dto.story_id,
            country="Belarus"
        )

        to_kafka = {
            "id": self._generate_id(),
            "content": dto.content,
            "story_id": dto.story_id,
            "country": "Belarus",
            "state": "PENDING",
        }

        if use(to_kafka):
            created = self._notice_repository.create(entity)
            return self._to_response(created)

    def get_notice(self, notice_id: int) -> Optional[NoticeResponseTo]:
        entity = self._notice_repository.get_by_id(notice_id)
        return self._to_response(entity) if entity else None

    def get_all_notices(self) -> List[NoticeResponseTo]:
        return [self._to_response(n) for n in self._notice_repository.get_all()]

    def update_notice(self, notice_id: int, dto: NoticeRequestTo) -> Optional[NoticeResponseTo]:
        existing = self._notice_repository.get_by_id(notice_id)
        if existing is None:
            return None

        if len(dto.content) < 2 or len(dto.content) > 2048:
            raise ValueError("Content must be between 2 and 2048 characters")

        entity = Notice(
            id=notice_id,
            content=dto.content,
            story_id=dto.story_id,
            country=existing.country  # Сохраняем существующую страну
        )

        updated = self._notice_repository.update(entity)
        return self._to_response(updated)

    def delete_notice(self, notice_id: int) -> bool:
        existing = self._notice_repository.get_by_id(notice_id)
        if existing is None:
            return False

        self._notice_repository.delete(existing.country, existing.story_id, notice_id)
        return True

    def get_notices_by_story(self, story_id: int) -> List[NoticeResponseTo]:
        # В Cassandra нужно указывать partition key (country)
        # Для упрощения используем get_all и фильтруем
        notices = [n for n in self._notice_repository.get_all() if n.story_id == story_id]
        return [self._to_response(n) for n in notices]

    @staticmethod
    def _to_response(entity: Notice) -> NoticeResponseTo:
        return NoticeResponseTo(
            id=entity.id,
            content=entity.content,
            story_id=entity.story_id,
            links={"self": f"/api/v1.0/notices/{entity.id}"},
        )