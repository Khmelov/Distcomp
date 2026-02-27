from datetime import datetime

from src.domain.models import Topic
from src.domain.repositories.interfaces import InMemoryRepository


class InMemoryTopicRepository(InMemoryRepository[Topic]):
    def create(self, topic: Topic) -> Topic:
        new_id = self._next_id()
        topic.id = new_id
        topic.created_at = datetime.now()
        topic.updated_at = datetime.now()
        self._data[new_id] = topic
        return topic

    def update(self, topic: Topic) -> Topic:
        topic.created_at = self._data[topic.id].created_at
        topic.updated_at = datetime.now()
        self._data[topic.id] = topic
        return topic