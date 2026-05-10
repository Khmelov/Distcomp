from datetime import datetime
from typing import List, Optional
from sqlalchemy import select
from sqlalchemy.orm import Session

from app.dtos.author_response import AuthorResponseTo
from app.dtos.mark_response import MarkResponseTo
from app.dtos.news_request import NewsRequestTo
from app.dtos.news_response import NewsResponseTo
from app.db.orm import AuthorOrm, MarkOrm, NewsOrm
from app.repositories.sqlalchemy_repository import PageParams, SqlAlchemyRepository

class NewsService:
    def __init__(self, db: Session) -> None:
        self._db = db
        self._repo = SqlAlchemyRepository[NewsOrm](db, NewsOrm)
        self._author_repo = SqlAlchemyRepository[AuthorOrm](db, AuthorOrm)
        self._mark_repo = SqlAlchemyRepository[MarkOrm](db, MarkOrm)

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
        news = NewsOrm(author_id=dto.authorId, title=dto.title, content=dto.content, created=now, modified=now)
        if dto.markIds:
            marks = list(self._db.execute(select(MarkOrm).where(MarkOrm.id.in_(dto.markIds))).scalars().all())
            news.marks = marks
        created = self._repo.create(news)
        return NewsResponseTo(
            id=created.id,
            authorId=created.author_id,
            title=created.title,
            content=created.content,
            created=created.created,
            modified=created.modified,
            markIds=[m.id for m in created.marks],
        )

    def get_news(self, news_id: int) -> Optional[NewsResponseTo]:
        entity = self._repo.get_by_id(news_id)
        if not entity:
            return None
        return NewsResponseTo(
            id=entity.id,
            authorId=entity.author_id,
            title=entity.title,
            content=entity.content,
            created=entity.created,
            modified=entity.modified,
            markIds=[m.id for m in entity.marks],
        )

    def get_all_news(
        self,
        page: PageParams,
        author_id: Optional[int] = None,
        title: Optional[str] = None,
        content: Optional[str] = None,
        mark_id: Optional[int] = None,
    ) -> List[NewsResponseTo]:
        stmt = select(NewsOrm).distinct()
        if author_id is not None:
            stmt = stmt.where(NewsOrm.author_id == author_id)
        if title:
            stmt = stmt.where(NewsOrm.title.ilike(f"%{title}%"))
        if content:
            stmt = stmt.where(NewsOrm.content.ilike(f"%{content}%"))
        if mark_id is not None:
            stmt = stmt.join(NewsOrm.marks).where(MarkOrm.id == mark_id)
        items = self._repo.list(stmt, page)
        return [
            NewsResponseTo(
                id=i.id,
                authorId=i.author_id,
                title=i.title,
                content=i.content,
                created=i.created,
                modified=i.modified,
                markIds=[m.id for m in i.marks],
            )
            for i in items
        ]

    def update_news(self, news_id: int, dto: NewsRequestTo) -> Optional[NewsResponseTo]:
        existing = self._repo.get_by_id(news_id)
        if not existing:
            return None
        self._ensure_author_exists(dto.authorId)
        self._ensure_marks_exist(dto.markIds)
        existing.author_id = dto.authorId
        existing.title = dto.title
        existing.content = dto.content
        existing.modified = datetime.utcnow()
        marks = []
        if dto.markIds:
            marks = list(self._db.execute(select(MarkOrm).where(MarkOrm.id.in_(dto.markIds))).scalars().all())
        existing.marks = marks
        self._db.commit()
        self._db.refresh(existing)
        return NewsResponseTo(
            id=existing.id,
            authorId=existing.author_id,
            title=existing.title,
            content=existing.content,
            created=existing.created,
            modified=existing.modified,
            markIds=[m.id for m in existing.marks],
        )

    def delete_news(self, news_id: int) -> bool:
        return self._repo.delete(news_id)

    def get_author_by_news(self, news_id: int) -> Optional[AuthorResponseTo]:
        news = self._repo.get_by_id(news_id)
        if not news:
            return None
        author = self._author_repo.get_by_id(news.author_id)
        if not author:
            return None
        return AuthorResponseTo(id=author.id, login=author.login, firstname=author.firstname, lastname=author.lastname)

    def get_marks_by_news(self, news_id: int) -> Optional[List[MarkResponseTo]]:
        news = self._repo.get_by_id(news_id)
        if not news:
            return None
        return [MarkResponseTo(id=m.id, name=m.name) for m in news.marks]
