from typing import List, Optional

from fastapi import APIRouter, Body, Depends
from sqlalchemy.orm import Session

from src.core.database import get_db
from src.schemas.dto import NewsRequestTo, NewsResponseTo, LabelResponseTo, NoteResponseTo, WriterResponseTo
from src.services import NewsService, WriterService, NoteService

router = APIRouter(prefix="/news")


@router.post("", response_model=NewsResponseTo, status_code=201)
def create_news(dto: NewsRequestTo = Body(...), db: Session = Depends(get_db)):
    return NewsService.create(db, dto)


@router.get("", response_model=List[NewsResponseTo])
def get_news(db: Session = Depends(get_db)):
    return NewsService.get_all(db)


@router.get("/search", response_model=List[NewsResponseTo])
def search_news(
    db: Session = Depends(get_db),
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
def get_writer_by_news(news_id: int, db: Session = Depends(get_db)):
    return WriterService.get_by_news_id(db, news_id)


@router.get("/{news_id}/labels", response_model=List[LabelResponseTo])
def get_labels_by_news(news_id: int, db: Session = Depends(get_db)):
    return NewsService.labels_by_news_id(db, news_id)


@router.get("/{news_id}/notes", response_model=List[NoteResponseTo])
def get_notes_by_news(news_id: int, db: Session = Depends(get_db)):
    return NoteService.by_news_id(db, news_id)


@router.get("/{id}", response_model=NewsResponseTo)
def get_news_by_id(id: int, db: Session = Depends(get_db)):
    return NewsService.get_by_id(db, id)


@router.put("/{id}", response_model=NewsResponseTo)
def update_news(id: int, dto: NewsRequestTo = Body(...), db: Session = Depends(get_db)):
    return NewsService.update(db, id, dto)


@router.delete("/{id}", status_code=204)
def delete_news(id: int, db: Session = Depends(get_db)):
    NewsService.delete(db, id)
