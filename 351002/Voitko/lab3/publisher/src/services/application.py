from datetime import datetime, timezone
from typing import Any, List, Optional

import httpx
from sqlalchemy import func, select
from sqlalchemy.orm import Session

from src.core.discussion_http import get_discussion_client
from src.core.errors import AppError
from src.domain.models.models import Writer, News, Label, tbl_news_label
from src.persistence.stores import (
    writer_store,
    news_store,
    label_store,
    PageRequest,
    NewsSearchCriteria,
)
from src.schemas.dto import (
    WriterRequestTo,
    WriterResponseTo,
    NewsRequestTo,
    NewsResponseTo,
    LabelRequestTo,
    LabelResponseTo,
    NoteRequestTo,
    NoteResponseTo,
)


def _news_to_response(n: News) -> NewsResponseTo:
    return NewsResponseTo(
        id=n.id,
        writerId=n.writer_id,
        title=n.title,
        content=n.content,
        created=n.created,
        modified=n.modified,
    )


def _delete_orphan_labels(db: Session, label_ids: list[int]) -> None:
    """Удаляет метки, на которые больше нет ссылок в tbl_news_label (ожидается в автотестах)."""
    if not label_ids:
        return
    for lid in dict.fromkeys(label_ids):
        n_links = (
            db.scalar(
                select(func.count())
                .select_from(tbl_news_label)
                .where(tbl_news_label.c.label_id == lid)
            )
            or 0
        )
        if n_links > 0:
            continue
        lab = db.get(Label, lid)
        if lab is not None:
            db.delete(lab)
    db.commit()


def _sync_news_labels(db: Session, news: News, dto: NewsRequestTo) -> None:
    """
    Связь News ↔ Label. Если в запросе переданы labelIds и/или labelNames — заменяет набор меток.
    По имени метка создаётся, если ещё нет (сценарии автотестов «News with N labels»).
    """
    if dto.labelIds is None and dto.labelNames is None:
        return

    labels: list[Label] = []
    for lid in dto.labelIds or []:
        lab = label_store.get_by_id(db, lid)
        if not lab:
            raise AppError(400, 40005, "Label not found")
        labels.append(lab)

    for raw in dto.labelNames or []:
        if raw is None:
            continue
        name = raw.strip()
        if len(name) < 2:
            continue
        if len(name) > 32:
            raise AppError(400, 40006, "Label name too long")
        lab = db.scalar(select(Label).where(Label.name == name))
        if lab is None:
            lab = Label(name=name)
            db.add(lab)
            db.flush()
        labels.append(lab)

    seen: set[int] = set()
    deduped: list[Label] = []
    for lab in labels:
        if lab.id in seen:
            continue
        seen.add(lab.id)
        deduped.append(lab)
    news.labels = deduped


class WriterService:
    @staticmethod
    def create(db: Session, dto: WriterRequestTo):
        if writer_store.exists_login(db, dto.login):
            raise AppError(403, 40302, "Writer already exists")

        entity = Writer(
            login=dto.login,
            password=dto.password,
            firstname=dto.firstname,
            lastname=dto.lastname,
        )
        w = writer_store.save(db, entity)
        return WriterResponseTo(
            id=w.id, login=w.login, firstname=w.firstname, lastname=w.lastname
        )

    @staticmethod
    def get_all(db: Session) -> List[WriterResponseTo]:
        pr = PageRequest()
        page = writer_store.find_all_page(db, pr)
        return [
            WriterResponseTo(id=w.id, login=w.login, firstname=w.firstname, lastname=w.lastname)
            for w in page.content
        ]

    @staticmethod
    def get_by_id(db: Session, id: int):
        w = writer_store.get_by_id(db, id)
        if not w:
            raise AppError(404, 40401, "Writer not found")
        return WriterResponseTo(
            id=w.id, login=w.login, firstname=w.firstname, lastname=w.lastname
        )

    @staticmethod
    def update(db: Session, id: int, dto: WriterRequestTo):
        w = writer_store.get_by_id(db, id)
        if not w:
            raise AppError(404, 40401, "Writer not found")

        if writer_store.exists_login(db, dto.login, exclude_id=id):
            raise AppError(403, 40302, "Writer already exists")

        w.login = dto.login
        w.password = dto.password
        w.firstname = dto.firstname
        w.lastname = dto.lastname
        writer_store.save(db, w)
        return WriterResponseTo(
            id=w.id, login=w.login, firstname=w.firstname, lastname=w.lastname
        )

    @staticmethod
    def delete(db: Session, id: int):
        if not writer_store.delete_by_id(db, id):
            raise AppError(404, 40401, "Writer not found")

    @staticmethod
    def get_by_news_id(db: Session, news_id: int) -> WriterResponseTo:
        n = news_store.get_by_id(db, news_id)
        if not n:
            raise AppError(404, 40402, "News not found")
        w = writer_store.get_by_id(db, n.writer_id)
        if not w:
            raise AppError(404, 40401, "Writer not found")
        return WriterResponseTo(
            id=w.id, login=w.login, firstname=w.firstname, lastname=w.lastname
        )


class NewsService:
    @staticmethod
    def create(db: Session, dto: NewsRequestTo):
        wr = writer_store.get_by_id(db, dto.writerId)
        if not wr:
            raise AppError(400, 40003, "Writer not found")

        if db.scalar(select(News.id).where(News.title == dto.title)) is not None:
            raise AppError(403, 40303, "News title already exists")

        now = datetime.now(timezone.utc)
        entity = News(
            writer_id=wr.id,
            writer=wr,
            title=dto.title,
            content=dto.content,
            created=now,
            modified=now,
        )
        n = news_store.save(db, entity)
        if dto.labelIds is not None or dto.labelNames is not None:
            linked = news_store.get_by_id(db, n.id)
            _sync_news_labels(db, linked, dto)
            news_store.save(db, linked)
            n = linked
        return _news_to_response(n)

    @staticmethod
    def get_all(db: Session) -> List[NewsResponseTo]:
        page = news_store.find_all_page(db, PageRequest())
        return [_news_to_response(n) for n in page.content]

    @staticmethod
    def get_by_id(db: Session, id: int):
        n = news_store.get_by_id(db, id)
        if not n:
            raise AppError(404, 40402, "News not found")
        return _news_to_response(n)

    @staticmethod
    def update(db: Session, id: int, dto: NewsRequestTo):
        n = news_store.get_by_id(db, id)
        if not n:
            raise AppError(404, 40402, "News not found")

        wr = writer_store.get_by_id(db, dto.writerId)
        if not wr:
            raise AppError(400, 40003, "Writer not found")

        other = db.scalar(
            select(News.id).where(News.title == dto.title, News.id != id)
        )
        if other is not None:
            raise AppError(403, 40303, "News title already exists")

        n.writer_id = wr.id
        n.writer = wr
        n.title = dto.title
        n.content = dto.content
        n.modified = datetime.now(timezone.utc)
        news_store.save(db, n)
        if dto.labelIds is not None or dto.labelNames is not None:
            fresh = news_store.get_by_id(db, n.id)
            _sync_news_labels(db, fresh, dto)
            news_store.save(db, fresh)
            n = fresh
        return _news_to_response(n)

    @staticmethod
    def delete(db: Session, id: int):
        if not news_store.get_by_id(db, id):
            raise AppError(404, 40402, "News not found")
        label_ids = list(
            db.scalars(
                select(tbl_news_label.c.label_id).where(
                    tbl_news_label.c.news_id == id
                )
            ).all()
        )
        NoteService.delete_all_for_news(id)
        if not news_store.delete_by_id(db, id):
            raise AppError(404, 40402, "News not found")
        _delete_orphan_labels(db, label_ids)

    @staticmethod
    def labels_by_news_id(db: Session, news_id: int) -> List[LabelResponseTo]:
        n = news_store.get_by_id(db, news_id)
        if not n:
            raise AppError(404, 40402, "News not found")
        db.refresh(n, ["labels"])
        return [LabelResponseTo(id=l.id, name=l.name) for l in n.labels]

    @staticmethod
    def search(
        db: Session,
        *,
        label_ids: Optional[List[int]] = None,
        label_names: Optional[List[str]] = None,
        writer_login: Optional[str] = None,
        title: Optional[str] = None,
        content: Optional[str] = None,
    ) -> List[NewsResponseTo]:
        crit = NewsSearchCriteria(
            label_ids=list(label_ids or []),
            label_names=list(label_names or []),
            writer_login=writer_login,
            title_contains=title,
            content_contains=content,
        )
        page = news_store.search_page(db, crit, PageRequest())
        return [_news_to_response(n) for n in page.content]


class LabelService:
    @staticmethod
    def create(db: Session, dto: LabelRequestTo):
        l = label_store.save(db, Label(name=dto.name))
        return LabelResponseTo(id=l.id, name=l.name)

    @staticmethod
    def get_all(db: Session) -> List[LabelResponseTo]:
        page = label_store.find_all_page(db, PageRequest())
        return [LabelResponseTo(id=l.id, name=l.name) for l in page.content]

    @staticmethod
    def get_by_id(db: Session, id: int):
        l = label_store.get_by_id(db, id)
        if not l:
            raise AppError(404, 40403, "Label not found")
        return LabelResponseTo(id=l.id, name=l.name)

    @staticmethod
    def update(db: Session, id: int, dto: LabelRequestTo):
        l = label_store.get_by_id(db, id)
        if not l:
            raise AppError(404, 40403, "Label not found")

        l.name = dto.name
        label_store.save(db, l)
        return LabelResponseTo(id=l.id, name=l.name)

    @staticmethod
    def delete(db: Session, id: int):
        if not label_store.delete_by_id(db, id):
            raise AppError(404, 40403, "Label not found")


class NoteService:
    """Заметки хранятся в модуле discussion (Cassandra); publisher обращается по HTTP."""

    @staticmethod
    def _http() -> httpx.Client:
        return get_discussion_client()

    @staticmethod
    def _body_or_none(r: httpx.Response) -> Any:
        if r.status_code == 204 or not r.content:
            return None
        return r.json()

    @staticmethod
    def _raise_app_error(r: httpx.Response) -> None:
        try:
            j = r.json()
            code = int(j.get("errorCode", 0))
            msg = str(j.get("errorMessage", r.text))
        except Exception:
            code = 0
            msg = r.text
        raise AppError(r.status_code, code, msg)

    @staticmethod
    def create(db: Session, dto: NoteRequestTo) -> NoteResponseTo:
        if not news_store.get_by_id(db, dto.newsId):
            raise AppError(400, 40004, "News not found")
        r = NoteService._http().post("/api/v1.0/notes", json=dto.model_dump())
        if r.is_error:
            NoteService._raise_app_error(r)
        return NoteResponseTo.model_validate(NoteService._body_or_none(r))

    @staticmethod
    def get_all(db: Session) -> List[NoteResponseTo]:
        r = NoteService._http().get("/api/v1.0/notes")
        if r.is_error:
            NoteService._raise_app_error(r)
        data = NoteService._body_or_none(r) or []
        return [NoteResponseTo.model_validate(x) for x in data]

    @staticmethod
    def get_by_id(db: Session, id: int):
        r = NoteService._http().get(f"/api/v1.0/notes/{id}")
        if r.is_error:
            NoteService._raise_app_error(r)
        return NoteResponseTo.model_validate(NoteService._body_or_none(r))

    @staticmethod
    def update(db: Session, id: int, dto: NoteRequestTo):
        if not news_store.get_by_id(db, dto.newsId):
            raise AppError(400, 40004, "News not found")
        r = NoteService._http().put(f"/api/v1.0/notes/{id}", json=dto.model_dump())
        if r.is_error:
            NoteService._raise_app_error(r)
        return NoteResponseTo.model_validate(NoteService._body_or_none(r))

    @staticmethod
    def delete(db: Session, id: int):
        r = NoteService._http().delete(f"/api/v1.0/notes/{id}")
        if r.is_error:
            NoteService._raise_app_error(r)

    @staticmethod
    def by_news_id(db: Session, news_id: int) -> List[NoteResponseTo]:
        if not news_store.get_by_id(db, news_id):
            raise AppError(404, 40402, "News not found")
        r = NoteService._http().get(f"/api/v1.0/notes/by-news/{news_id}")
        if r.is_error:
            NoteService._raise_app_error(r)
        data = NoteService._body_or_none(r) or []
        return [NoteResponseTo.model_validate(x) for x in data]

    @staticmethod
    def delete_all_for_news(news_id: int) -> None:
        r = NoteService._http().delete(f"/api/v1.0/notes/by-news/{news_id}")
        if r.status_code not in (204, 404) and r.is_error:
            NoteService._raise_app_error(r)
