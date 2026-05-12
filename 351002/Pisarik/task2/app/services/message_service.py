from typing import List, Optional

from sqlalchemy import select
from sqlalchemy.orm import Session

from app.dtos.message_request import MessageRequestTo
from app.dtos.message_response import MessageResponseTo
from app.db.orm import MessageOrm, NewsOrm
from app.repositories.sqlalchemy_repository import PageParams, SqlAlchemyRepository

class MessageService:
    def __init__(self, db: Session) -> None:
        self._db = db
        self._repo = SqlAlchemyRepository[MessageOrm](db, MessageOrm)
        self._news_repo = SqlAlchemyRepository[NewsOrm](db, NewsOrm)

    def _ensure_news_exists(self, news_id: int) -> None:
        news = self._news_repo.get_by_id(news_id)
        if not news:
            raise ValueError(f"News with id {news_id} not found")

    def create_message(self, dto: MessageRequestTo) -> MessageResponseTo:
        self._ensure_news_exists(dto.newsId)
        created = self._repo.create(MessageOrm(news_id=dto.newsId, content=dto.content))
        return MessageResponseTo(id=created.id, newsId=created.news_id, content=created.content)

    def get_message(self, message_id: int) -> Optional[MessageResponseTo]:
        entity = self._repo.get_by_id(message_id)
        if not entity:
            return None
        return MessageResponseTo(id=entity.id, newsId=entity.news_id, content=entity.content)

    def get_all_messages(
        self,
        page: PageParams,
        news_id: Optional[int] = None,
        content: Optional[str] = None,
    ) -> List[MessageResponseTo]:
        stmt = select(MessageOrm)
        if news_id is not None:
            stmt = stmt.where(MessageOrm.news_id == news_id)
        if content:
            stmt = stmt.where(MessageOrm.content.ilike(f"%{content}%"))
        items = self._repo.list(stmt, page)
        return [MessageResponseTo(id=i.id, newsId=i.news_id, content=i.content) for i in items]

    def get_messages_by_news(self, news_id: int) -> List[MessageResponseTo]:
        self._ensure_news_exists(news_id)
        stmt = select(MessageOrm).where(MessageOrm.news_id == news_id)
        items = list(self._db.execute(stmt).scalars().all())
        return [MessageResponseTo(id=i.id, newsId=i.news_id, content=i.content) for i in items]

    def update_message(self, message_id: int, dto: MessageRequestTo) -> Optional[MessageResponseTo]:
        existing = self._repo.get_by_id(message_id)
        if not existing:
            return None
        self._ensure_news_exists(dto.newsId)
        existing.news_id = dto.newsId
        existing.content = dto.content
        self._db.commit()
        self._db.refresh(existing)
        return MessageResponseTo(id=existing.id, newsId=existing.news_id, content=existing.content)

    def delete_message(self, message_id: int) -> bool:
        return self._repo.delete(message_id)
