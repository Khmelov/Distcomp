from __future__ import annotations

from abc import ABC, abstractmethod
from dataclasses import dataclass, field
from typing import Any, Callable, Generic, List, Optional, TypeVar

from sqlalchemy import Select, and_, func, select, exists
from sqlalchemy.orm import Session

from src.domain.models.models import Writer, News, Label, Note, tbl_news_label

T = TypeVar("T")
ID = TypeVar("ID", int, str)


@dataclass
class PageRequest:
    page: int = 0
    size: int = 1000
    sort_field: Optional[str] = None
    sort_desc: bool = False


@dataclass
class PageResult(Generic[T]):
    content: List[T]
    total_elements: int


FilterFn = Callable[[Select[Any]], Select[Any]]


class EntityStore(ABC, Generic[T, ID]):
    @abstractmethod
    def get_by_id(self, db: Session, entity_id: ID) -> Optional[T]:
        ...

    @abstractmethod
    def save(self, db: Session, entity: T) -> T:
        ...

    @abstractmethod
    def delete_by_id(self, db: Session, entity_id: ID) -> bool:
        ...

    @abstractmethod
    def find_all_page(
        self,
        db: Session,
        page: PageRequest,
        filter_fn: Optional[FilterFn] = None,
    ) -> PageResult[T]:
        ...


def _apply_sort(stmt: Select[Any], model: type, allow: frozenset[str], page: PageRequest) -> Select[Any]:
    if page.sort_field and page.sort_field in allow:
        col = getattr(model, page.sort_field)
        if page.sort_desc:
            return stmt.order_by(col.desc())
        return stmt.order_by(col.asc())
    return stmt.order_by(getattr(model, "id").asc())


def _page_execute(db: Session, stmt: Select[Any], page: PageRequest) -> PageResult:
    total = db.scalar(select(func.count()).select_from(stmt.subquery())) or 0
    rows = db.execute(stmt.offset(page.page * page.size).limit(page.size)).scalars().all()
    return PageResult(content=list(rows), total_elements=int(total))


class WriterStore(EntityStore[Writer, int]):
    _SORT = frozenset({"id", "login", "firstname", "lastname"})

    def get_by_id(self, db: Session, entity_id: int) -> Optional[Writer]:
        return db.get(Writer, entity_id)

    def save(self, db: Session, entity: Writer) -> Writer:
        db.add(entity)
        db.commit()
        db.refresh(entity)
        return entity

    def delete_by_id(self, db: Session, entity_id: int) -> bool:
        w = db.get(Writer, entity_id)
        if not w:
            return False
        db.delete(w)
        db.commit()
        return True

    def find_all_page(
        self,
        db: Session,
        page: PageRequest,
        filter_fn: Optional[FilterFn] = None,
    ) -> PageResult[Writer]:
        stmt = select(Writer)
        if filter_fn:
            stmt = filter_fn(stmt)
        stmt = _apply_sort(stmt, Writer, self._SORT, page)
        return _page_execute(db, stmt, page)

    def exists_login(self, db: Session, login: str, exclude_id: Optional[int] = None) -> bool:
        stmt = select(Writer.id).where(Writer.login == login)
        if exclude_id is not None:
            stmt = stmt.where(Writer.id != exclude_id)
        return db.scalar(stmt) is not None


@dataclass
class NewsSearchCriteria:
    label_ids: List[int] = field(default_factory=list)
    label_names: List[str] = field(default_factory=list)
    writer_login: Optional[str] = None
    title_contains: Optional[str] = None
    content_contains: Optional[str] = None


class NewsStore(EntityStore[News, int]):
    _SORT = frozenset({"id", "title", "created", "modified", "writer_id"})

    def get_by_id(self, db: Session, entity_id: int) -> Optional[News]:
        return db.get(News, entity_id)

    def save(self, db: Session, entity: News) -> News:
        db.add(entity)
        db.commit()
        db.refresh(entity)
        return entity

    def delete_by_id(self, db: Session, entity_id: int) -> bool:
        n = db.get(News, entity_id)
        if not n:
            return False
        db.delete(n)
        db.commit()
        return True

    def find_all_page(
        self,
        db: Session,
        page: PageRequest,
        filter_fn: Optional[FilterFn] = None,
    ) -> PageResult[News]:
        stmt = select(News)
        if filter_fn:
            stmt = filter_fn(stmt)
        stmt = _apply_sort(stmt, News, self._SORT, page)
        return _page_execute(db, stmt, page)

    def search_page(
        self,
        db: Session,
        criteria: NewsSearchCriteria,
        page: PageRequest,
    ) -> PageResult[News]:
        stmt = select(News)
        conds: List[Any] = []

        if criteria.writer_login:
            stmt = stmt.join(News.writer)
            conds.append(Writer.login == criteria.writer_login)

        if criteria.title_contains:
            conds.append(News.title.ilike(f"%{criteria.title_contains}%"))
        if criteria.content_contains:
            conds.append(News.content.ilike(f"%{criteria.content_contains}%"))

        for lid in criteria.label_ids or []:
            subq = (
                select(1)
                .select_from(tbl_news_label)
                .where(
                    and_(
                        tbl_news_label.c.news_id == News.id,
                        tbl_news_label.c.label_id == lid,
                    )
                )
            )
            conds.append(exists(subq))

        for name in criteria.label_names or []:
            if not name:
                continue
            subq = (
                select(1)
                .select_from(
                    tbl_news_label.join(Label, Label.id == tbl_news_label.c.label_id)
                )
                .where(
                    and_(
                        tbl_news_label.c.news_id == News.id,
                        Label.name == name,
                    )
                )
            )
            conds.append(exists(subq))

        if conds:
            stmt = stmt.where(and_(*conds))
        stmt = stmt.distinct()
        stmt = _apply_sort(stmt, News, self._SORT, page)
        return _page_execute(db, stmt, page)


class LabelStore(EntityStore[Label, int]):
    _SORT = frozenset({"id", "name"})

    def get_by_id(self, db: Session, entity_id: int) -> Optional[Label]:
        return db.get(Label, entity_id)

    def save(self, db: Session, entity: Label) -> Label:
        db.add(entity)
        db.commit()
        db.refresh(entity)
        return entity

    def delete_by_id(self, db: Session, entity_id: int) -> bool:
        l = db.get(Label, entity_id)
        if not l:
            return False
        db.delete(l)
        db.commit()
        return True

    def find_all_page(
        self,
        db: Session,
        page: PageRequest,
        filter_fn: Optional[FilterFn] = None,
    ) -> PageResult[Label]:
        stmt = select(Label)
        if filter_fn:
            stmt = filter_fn(stmt)
        stmt = _apply_sort(stmt, Label, self._SORT, page)
        return _page_execute(db, stmt, page)


class NoteStore(EntityStore[Note, int]):
    _SORT = frozenset({"id", "news_id"})

    def get_by_id(self, db: Session, entity_id: int) -> Optional[Note]:
        return db.get(Note, entity_id)

    def save(self, db: Session, entity: Note) -> Note:
        db.add(entity)
        db.commit()
        db.refresh(entity)
        return entity

    def delete_by_id(self, db: Session, entity_id: int) -> bool:
        n = db.get(Note, entity_id)
        if not n:
            return False
        db.delete(n)
        db.commit()
        return True

    def find_all_page(
        self,
        db: Session,
        page: PageRequest,
        filter_fn: Optional[FilterFn] = None,
    ) -> PageResult[Note]:
        stmt = select(Note)
        if filter_fn:
            stmt = filter_fn(stmt)
        stmt = _apply_sort(stmt, Note, self._SORT, page)
        return _page_execute(db, stmt, page)

    def find_by_news_id_page(
        self,
        db: Session,
        news_id: int,
        page: PageRequest,
    ) -> PageResult[Note]:
        def _f(s: Select[Any]) -> Select[Any]:
            return s.where(Note.news_id == news_id)

        return self.find_all_page(db, page, filter_fn=_f)


writer_store = WriterStore()
news_store = NewsStore()
label_store = LabelStore()
note_store = NoteStore()
