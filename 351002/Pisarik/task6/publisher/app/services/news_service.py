from datetime import datetime
from typing import List, Optional

from sqlalchemy import select
from sqlalchemy.orm import Session

from app.dtos.author_response import AuthorResponseTo
from app.dtos.mark_response import MarkResponseTo
from app.dtos.news_request import NewsRequestTo
from app.dtos.news_response import NewsResponseTo
from app.db.orm import AuthorOrm, MarkOrm, NewsOrm
from app.redis_cache import cache_delete, cache_get_json, cache_set_json, entity_id_key, entity_list_key, invalidate_entity
from app.repositories.sqlalchemy_repository import PageParams, SqlAlchemyRepository


def _news_to_dto(entity: NewsOrm) -> NewsResponseTo:
    return NewsResponseTo(
        id=entity.id,
        authorId=entity.author_id,
        title=entity.title,
        content=entity.content,
        created=entity.created,
        modified=entity.modified,
        markIds=[m.id for m in entity.marks],
    )


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
        invalidate_entity("news")
        out = _news_to_dto(created)
        cache_set_json(entity_id_key("news", out.id), out.model_dump(mode="json"))
        return out

    def get_news(self, news_id: int) -> Optional[NewsResponseTo]:
        k = entity_id_key("news", news_id)
        cached = cache_get_json(k)
        if isinstance(cached, dict):
            try:
                return NewsResponseTo.model_validate(cached)
            except Exception:
                pass
        entity = self._repo.get_by_id(news_id)
        if not entity:
            return None
        out = _news_to_dto(entity)
        cache_set_json(k, out.model_dump(mode="json"))
        return out

    def get_all_news(
        self,
        page: PageParams,
        author_id: Optional[int] = None,
        title: Optional[str] = None,
        content: Optional[str] = None,
        mark_id: Optional[int] = None,
    ) -> List[NewsResponseTo]:
        params = {
            "page": page.page,
            "size": page.size,
            "sort": page.sort,
            "authorId": author_id,
            "title": title or "",
            "content": content or "",
            "markId": mark_id,
        }
        lk = entity_list_key("news", params)
        cached = cache_get_json(lk)
        if isinstance(cached, list):
            try:
                return [NewsResponseTo.model_validate(x) for x in cached]
            except Exception:
                pass
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
        out = [_news_to_dto(i) for i in items]
        cache_set_json(lk, [x.model_dump(mode="json") for x in out])
        return out

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
        invalidate_entity("news", news_id)
        cache_delete(f"{entity_id_key('news', news_id)}:author")
        cache_delete(f"{entity_id_key('news', news_id)}:marks")
        out = _news_to_dto(existing)
        cache_set_json(entity_id_key("news", out.id), out.model_dump(mode="json"))
        return out

    def delete_news(self, news_id: int) -> bool:
        ok = self._repo.delete(news_id)
        if ok:
            invalidate_entity("news", news_id)
            cache_delete(f"{entity_id_key('news', news_id)}:author")
            cache_delete(f"{entity_id_key('news', news_id)}:marks")
        return ok

    def get_author_by_news(self, news_id: int) -> Optional[AuthorResponseTo]:
        subk = f"{entity_id_key('news', news_id)}:author"
        cached = cache_get_json(subk)
        if isinstance(cached, dict):
            try:
                return AuthorResponseTo.model_validate(cached)
            except Exception:
                pass
        news = self._repo.get_by_id(news_id)
        if not news:
            return None
        author = self._author_repo.get_by_id(news.author_id)
        if not author:
            return None
        out = AuthorResponseTo(id=author.id, login=author.login, firstname=author.firstname, lastname=author.lastname)
        cache_set_json(subk, out.model_dump(mode="json"))
        return out

    def get_marks_by_news(self, news_id: int) -> Optional[List[MarkResponseTo]]:
        subk = f"{entity_id_key('news', news_id)}:marks"
        cached = cache_get_json(subk)
        if isinstance(cached, list):
            try:
                return [MarkResponseTo.model_validate(x) for x in cached]
            except Exception:
                pass
        news = self._repo.get_by_id(news_id)
        if not news:
            return None
        out = [MarkResponseTo(id=m.id, name=m.name) for m in news.marks]
        cache_set_json(subk, [x.model_dump(mode="json") for x in out])
        return out
