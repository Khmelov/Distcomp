from models.topic import Topic
from dto.requests import TopicRequestTo
from dto.responses import TopicResponseTo
from .base import BaseService
from repository.interface import IRepository


class TopicService(BaseService[Topic, TopicRequestTo, TopicResponseTo]):
    def __init__(
        self, topic_repo: IRepository[Topic], user_repo: IRepository, marker_repo
    ):
        super().__init__(topic_repo)
        self._user_repo = user_repo
        self._marker_repo = marker_repo

    def create(self, request: TopicRequestTo) -> TopicResponseTo:
        if not self._user_repo.get(request.userId):
            raise ValueError("User not found")

        # Собираем итоговые markerIds
        marker_ids = set(request.markerIds)  # из переданных чисел

        # Обрабатываем имена маркеров
        for name in request.markers:
            existing = self._marker_repo.get_by_name(name)
            if existing:
                marker_ids.add(existing.id)
            else:
                new_marker = self._marker_repo.create_by_name(name)
                marker_ids.add(new_marker.id)

        entity = self._to_entity(request)
        entity.markerIds = list(marker_ids)
        created = self._repo.create(entity)
        return self._to_response(created)

    def _to_entity(self, request: TopicRequestTo) -> Topic:
        return Topic(
            userId=request.userId,
            title=request.title,
            content=request.content,
            markerIds=[],
        )

    def _to_response(self, entity: Topic) -> TopicResponseTo:
        return TopicResponseTo(
            id=entity.id,
            userId=entity.userId,
            title=entity.title,
            content=entity.content,
            markerIds=entity.markerIds,
            created=entity.created,
            modified=entity.modified,
        )
