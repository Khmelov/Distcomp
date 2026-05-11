from datetime import datetime
from typing import List, Optional
from app.models.author import Author
from app.models.mark import Mark
from app.models.news import News
from app.repositories.in_memory_repository import InMemoryRepository
from app.dtos.author_response import AuthorResponseTo
from app.dtos.mark_response import MarkResponseTo
from app.dtos.news_request import NewsRequestTo
from app.dtos.news_response import NewsResponseTo

class NewsService:
    def __init__(self, repo: InMemoryRepository[News], author_repo: InMemoryRepository[Author], mark_repo: InMemoryRepository[Mark]) -> None:
        self._repo = repo
        self._author_repo = author_repo
        self._mark_repo = mark_repo

    def _ensure_author_exists(self, author_id: int) -> None:
        author = self._author_repo.get_by_id(author_id)
        if not author:
            raise ValueError(f"Author with id {author_id} not found")

    def _ensure_marks_exist(self, mark_ids: List[int]) -> None:
        missing = [mark_id for mark_id in mark_ids if not self._mark_repo.get_by_id(mark_id)]
        if missing:
            raise ValueError(f"Marks not found: {missing}")

    def create_news(self, dto: NewsRequestTo) -> NewsResponseTo:
        self._ensure_author_exists(dto.authorId)
        self._ensure_marks_exist(dto.markIds)
        now = datetime.utcnow()
        entity = News(id=self._repo.next_id(), authorId=dto.authorId, title=dto.title, content=dto.content, created=now, modified=now, markIds=dto.markIds)
        created = self._repo.create(entity)
        return NewsResponseTo(id=created.id, authorId=created.authorId, title=created.title, content=created.content, created=created.created, modified=created.modified, markIds=created.markIds)

    def get_news(self, news_id: int) -> Optional[NewsResponseTo]:
        entity = self._repo.get_by_id(news_id)
        if not entity:
            return None
        return NewsResponseTo(id=entity.id, authorId=entity.authorId, title=entity.title, content=entity.content, created=entity.created, modified=entity.modified, markIds=entity.markIds)

    def get_all_news(self) -> List[NewsResponseTo]:
        return [NewsResponseTo(id=i.id, authorId=i.authorId, title=i.title, content=i.content, created=i.created, modified=i.modified, markIds=i.markIds) for i in self._repo.get_all()]

    def update_news(self, news_id: int, dto: NewsRequestTo) -> Optional[NewsResponseTo]:
        existing = self._repo.get_by_id(news_id)
        if not existing:
            return None
        self._ensure_author_exists(dto.authorId)
        self._ensure_marks_exist(dto.markIds)
        updated_entity = News(id=news_id, authorId=dto.authorId, title=dto.title, content=dto.content, created=existing.created, modified=datetime.utcnow(), markIds=dto.markIds)
        updated = self._repo.update(news_id, updated_entity)
        if not updated:
            return None
        return NewsResponseTo(id=updated.id, authorId=updated.authorId, title=updated.title, content=updated.content, created=updated.created, modified=updated.modified, markIds=updated.markIds)

    def delete_news(self, news_id: int) -> bool:
        return self._repo.delete(news_id)

    def get_author_by_news(self, news_id: int) -> Optional[AuthorResponseTo]:
        news = self._repo.get_by_id(news_id)
        if not news:
            return None
        author = self._author_repo.get_by_id(news.authorId)
        if not author:
            return None
        return AuthorResponseTo(id=author.id, login=author.login, firstname=author.firstname, lastname=author.lastname)

    def get_marks_by_news(self, news_id: int) -> Optional[List[MarkResponseTo]]:
        news = self._repo.get_by_id(news_id)
        if not news:
            return None
        return [MarkResponseTo(id=mark.id, name=mark.name) for mark_id in news.markIds if (mark := self._mark_repo.get_by_id(mark_id))]
