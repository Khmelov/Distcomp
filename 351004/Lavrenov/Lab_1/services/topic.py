from models.topic import Topic
from dto.requests import TopicRequestTo
from dto.responses import TopicResponseTo
from .base import BaseService


class TopicService(BaseService[Topic, TopicRequestTo, TopicResponseTo]):
    def _to_entity(self, request: TopicRequestTo) -> Topic:
        return Topic(
            userId=request.userId,
            title=request.title,
            content=request.content,
            markerIds=request.markerIds or [],
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
