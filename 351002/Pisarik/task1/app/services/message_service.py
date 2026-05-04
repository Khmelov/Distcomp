from typing import List, Optional
from app.models.message import Message
from app.models.news import News
from app.repositories.in_memory_repository import InMemoryRepository
from app.dtos.message_request import MessageRequestTo
from app.dtos.message_response import MessageResponseTo

class MessageService:
    def __init__(self, repo: InMemoryRepository[Message], news_repo: InMemoryRepository[News]) -> None:
        self._repo = repo
        self._news_repo = news_repo

    def _ensure_news_exists(self, news_id: int) -> None:
        news = self._news_repo.get_by_id(news_id)
        if not news:
            raise ValueError(f"News with id {news_id} not found")

    def create_message(self, dto: MessageRequestTo) -> MessageResponseTo:
        self._ensure_news_exists(dto.newsId)
        entity = Message(id=self._repo.next_id(), newsId=dto.newsId, content=dto.content)
        created = self._repo.create(entity)
        return MessageResponseTo(id=created.id, newsId=created.newsId, content=created.content)

    def get_message(self, message_id: int) -> Optional[MessageResponseTo]:
        entity = self._repo.get_by_id(message_id)
        if not entity:
            return None
        return MessageResponseTo(id=entity.id, newsId=entity.newsId, content=entity.content)

    def get_all_messages(self) -> List[MessageResponseTo]:
        return [MessageResponseTo(id=i.id, newsId=i.newsId, content=i.content) for i in self._repo.get_all()]

    def get_messages_by_news(self, news_id: int) -> List[MessageResponseTo]:
        self._ensure_news_exists(news_id)
        return [MessageResponseTo(id=i.id, newsId=i.newsId, content=i.content) for i in self._repo.get_all() if i.newsId == news_id]

    def update_message(self, message_id: int, dto: MessageRequestTo) -> Optional[MessageResponseTo]:
        existing = self._repo.get_by_id(message_id)
        if not existing:
            return None
        self._ensure_news_exists(dto.newsId)
        updated_entity = Message(id=message_id, newsId=dto.newsId, content=dto.content)
        updated = self._repo.update(message_id, updated_entity)
        if not updated:
            return None
        return MessageResponseTo(id=updated.id, newsId=updated.newsId, content=updated.content)

    def delete_message(self, message_id: int) -> bool:
        return self._repo.delete(message_id)
