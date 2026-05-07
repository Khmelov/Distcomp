from typing import List

from src.core.constants import ErrorStatus
from src.core.errors import HttpNotFoundError, TopicErrorMessage
from src.domain.models import Topic
from src.domain.repositories.interfaces import Repository
from src.schemas.topic import TopicResponseTo, TopicRequestTo


class TopicService:
    def __init__(self, repo: Repository[Topic]):
        self._repo = repo

    def get_one(self, topic_id: int) -> TopicResponseTo:
        try:
            topic = self._repo.get_one(topic_id)
        except KeyError:
            raise HttpNotFoundError(TopicErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)
        return TopicResponseTo.model_validate(topic)

    def get_all(self) -> List[TopicResponseTo]:
        topics = self._repo.get_all()
        return [TopicResponseTo.model_validate(t) for t in topics]

    def create(self, dto: TopicRequestTo) -> TopicResponseTo:
        topic = Topic(
            id=0,
            title=dto.title,
            content=dto.content,
            author_id=dto.author_id,
        )
        created_topic = self._repo.create(topic)
        return TopicResponseTo.model_validate(created_topic)

    def update(self, topic_id: int, dto: TopicRequestTo) -> TopicResponseTo:
        topic = Topic(
            id=topic_id,
            title=dto.title,
            content=dto.content,
            author_id=dto.author_id,
        )
        try:
            updated_topic = self._repo.update(topic)
        except KeyError:
            raise HttpNotFoundError(TopicErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)
        return TopicResponseTo.model_validate(updated_topic)

    def delete(self, topic_id: int) -> None:
        try:
            self._repo.delete(topic_id)
        except KeyError:
            raise HttpNotFoundError(TopicErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)