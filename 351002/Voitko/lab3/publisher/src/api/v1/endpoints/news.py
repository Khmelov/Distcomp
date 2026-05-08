from typing import Any, List, Optional

from fastapi import APIRouter, Body

from src.api.v1.body_util import parse_wrapped
from src.api.v1.dep import SessionDep
from src.schemas.dto import NewsRequestTo, NewsResponseTo, LabelResponseTo, NoteResponseTo, WriterResponseTo
from src.services import NewsService, WriterService, NoteService

router = APIRouter(prefix="/news")


@router.post("", response_model=NewsResponseTo, status_code=201)
def create_news(db: SessionDep, body: dict[str, Any] = Body(...)):
    dto = parse_wrapped(body, ("news", "tweet"), NewsRequestTo)
    return NewsService.create(db, dto)


@router.get("", response_model=List[NewsResponseTo])
def get_news(db: SessionDep):
    return NewsService.get_all(db)


@router.get("/search", response_model=List[NewsResponseTo])
def search_news(
    db: SessionDep,
    label_ids: Optional[List[int]] = None,
    label_names: Optional[List[str]] = None,
    writer_login: Optional[str] = None,
    title: Optional[str] = None,
    content: Optional[str] = None,
):
    return NewsService.search(
        db,
        label_ids=label_ids,
        label_names=label_names,
        writer_login=writer_login,
        title=title,
        content=content,
    )


@router.get("/{news_id}/writer", response_model=WriterResponseTo)
def get_writer_by_news(news_id: int, db: SessionDep):
    return WriterService.get_by_news_id(db, news_id)


@router.get("/{news_id}/labels", response_model=List[LabelResponseTo])
def get_labels_by_news(news_id: int, db: SessionDep):
    return NewsService.labels_by_news_id(db, news_id)


@router.get("/{news_id}/notes", response_model=List[NoteResponseTo])
def get_notes_by_news(news_id: int, db: SessionDep):
    return NoteService.by_news_id(db, news_id)


@router.get("/{id}", response_model=NewsResponseTo)
def get_news_by_id(id: int, db: SessionDep):
    return NewsService.get_by_id(db, id)


@router.put("/{id}", response_model=NewsResponseTo)
def update_news(id: int, db: SessionDep, body: dict[str, Any] = Body(...)):
    dto = parse_wrapped(body, ("news", "tweet"), NewsRequestTo)
    return NewsService.update(db, id, dto)


@router.delete("/{id}", status_code=204)
def delete_news(id: int, db: SessionDep):
    NewsService.delete(db, id)
