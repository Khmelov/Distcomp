from app.repositories.base_repository import InMemoryRepository
from app.models.tweet import Tweet
from app.dtos.tweet_dto import TweetRequestTo, TweetResponseTo
from app.services.writer_service import writer_repository
from app.services.marker_service import marker_repository
from app.errors import AppError
from typing import List
from datetime import datetime

tweet_repository = InMemoryRepository[Tweet]()


class TweetService:
    def create(self, dto: TweetRequestTo) -> TweetResponseTo:
        # 1. Проверка существования Writer
        writer = writer_repository.get_by_id(dto.writer_id)
        if not writer:
            raise AppError(status_code=403, message="Writer not found", error_code=40303)

        # 2. Проверка существования маркеров
        if dto.marker_ids:
            for marker_id in dto.marker_ids:
                marker = marker_repository.get_by_id(marker_id)
                if not marker:
                    raise AppError(status_code=403, message=f"Marker {marker_id} not found", error_code=40304)

        # 3. Создание сущности
        entity = Tweet(
            writer_id=dto.writer_id,
            title=dto.title,
            content=dto.content,
            marker_ids=dto.marker_ids or [],
            created=datetime.now(),
            modified=datetime.now()
        )
        saved_entity = tweet_repository.create(entity)

        # 4. Явный маппинг в DTO (исправление ошибки 500)
        return TweetResponseTo(
            id=saved_entity.id,
            writerId=saved_entity.writer_id,
            title=saved_entity.title,
            content=saved_entity.content,
            created=saved_entity.created,
            modified=saved_entity.modified,
            markerIds=saved_entity.marker_ids
        )

    def get_all(self) -> List[TweetResponseTo]:
        entities = tweet_repository.get_all()
        return [
            TweetResponseTo(
                id=e.id,
                writerId=e.writer_id,
                title=e.title,
                content=e.content,
                created=e.created,
                modified=e.modified,
                markerIds=e.marker_ids
            ) for e in entities
        ]

    def get_by_id(self, id: int) -> TweetResponseTo:
        entity = tweet_repository.get_by_id(id)
        if not entity:
            raise AppError(status_code=404, message="Tweet not found", error_code=40404)

        return TweetResponseTo(
            id=entity.id,
            writerId=entity.writer_id,
            title=entity.title,
            content=entity.content,
            created=entity.created,
            modified=entity.modified,
            markerIds=entity.marker_ids
        )

    def update(self, id: int, dto: TweetRequestTo) -> TweetResponseTo:
        entity = tweet_repository.get_by_id(id)
        if not entity:
            raise AppError(status_code=404, message="Tweet not found", error_code=40405)

        # Проверка Writer
        writer = writer_repository.get_by_id(dto.writer_id)
        if not writer:
            raise AppError(status_code=403, message="Writer not found", error_code=40305)

        # Проверка Markers
        if dto.marker_ids:
            for marker_id in dto.marker_ids:
                marker = marker_repository.get_by_id(marker_id)
                if not marker:
                    raise AppError(status_code=403, message=f"Marker {marker_id} not found", error_code=40306)

        # Обновление
        updated_entity = Tweet(
            id=id,
            writer_id=dto.writer_id,
            title=dto.title,
            content=dto.content,
            marker_ids=dto.marker_ids or [],
            created=entity.created,  # дата создания не меняется
            modified=datetime.now()  # дата обновления меняется
        )
        tweet_repository.update(id, updated_entity)

        return TweetResponseTo(
            id=updated_entity.id,
            writerId=updated_entity.writer_id,
            title=updated_entity.title,
            content=updated_entity.content,
            created=updated_entity.created,
            modified=updated_entity.modified,
            markerIds=updated_entity.marker_ids
        )

    def delete(self, id: int) -> None:
        entity = tweet_repository.get_by_id(id)
        if not entity:
            raise AppError(status_code=404, message="Tweet not found", error_code=40406)
        tweet_repository.delete(id)
