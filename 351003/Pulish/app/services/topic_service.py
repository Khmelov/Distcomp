from app.dto.topic import TopicRequestTo, TopicResponseTo
from app.models.topic import Topic
from app.repository.inmemory import InMemoryRepository
from app.core.exceptions import NotFoundException


class TopicService:
    def __init__(self):
        self.repo = InMemoryRepository()

    def create(self, dto: TopicRequestTo) -> TopicResponseTo:
        topic = Topic(
            id=0,
            title=dto.title,
            content=dto.content,
            user_id=dto.userId,
            mark_ids=dto.markIds
        )
        saved = self.repo.create(topic)
        return self._to_response(saved)

    def find_all(self):
        return [self._to_response(t) for t in self.repo.find_all()]

    def find_by_id(self, id: int):
        topic = self.repo.find_by_id(id)
        if not topic:
            raise NotFoundException("Topic not found", 40402)
        return self._to_response(topic)

    def update(self, id: int, dto: TopicRequestTo):
        topic = self.repo.find_by_id(id)
        if not topic:
            raise NotFoundException("Topic not found", 40402)

        topic.title = dto.title
        topic.content = dto.content
        topic.user_id = dto.userId
        topic.mark_ids = dto.markIds

        return self._to_response(topic)

    def delete(self, id: int):
        topic = self.repo.find_by_id(id)
        if not topic:
            raise NotFoundException("Topic not found", 40401)
        self.repo.delete(id)

    def _to_response(self, topic: Topic) -> TopicResponseTo:
        return TopicResponseTo(
            id=topic.id,
            title=topic.title,
            content=topic.content,
            userId=topic.user_id,
            markIds=topic.mark_ids
        )
