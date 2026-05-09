from __future__ import annotations

from typing import List

from cassandra.cluster import Session

from discussion_mod.core.errors import AppError
from discussion_mod.domain.note_id import next_note_id
from discussion_mod.domain.repositories.note_repository import NoteRepository
from discussion_mod.schemas.note import NoteRequestTo, NoteResponseTo


class NoteService:
    def __init__(self, session: Session):
        self._repo = NoteRepository(session)

    def create(self, dto: NoteRequestTo) -> NoteResponseTo:
        note_id = next_note_id()
        self._repo.insert(dto.newsId, note_id, dto.content)
        return NoteResponseTo(id=note_id, newsId=dto.newsId, content=dto.content)

    def get_all(self) -> List[NoteResponseTo]:
        rows = self._repo.list_all()
        return [NoteResponseTo(id=i, newsId=n, content=c) for i, n, c in rows]

    def get_one(self, note_id: int) -> NoteResponseTo:
        row = self._repo.get_by_id(note_id)
        if row is None:
            raise AppError(404, 40404, "Note not found")
        i, n, c = row
        return NoteResponseTo(id=i, newsId=n, content=c)

    def update(self, note_id: int, dto: NoteRequestTo) -> NoteResponseTo:
        if self._repo.get_by_id(note_id) is None:
            raise AppError(404, 40404, "Note not found")
        self._repo.update(note_id, dto.newsId, dto.content)
        return NoteResponseTo(id=note_id, newsId=dto.newsId, content=dto.content)

    def delete(self, note_id: int) -> None:
        self._repo.delete(note_id)

    def by_news_id(self, news_id: int) -> List[NoteResponseTo]:
        rows = self._repo.list_by_news(news_id)
        return [NoteResponseTo(id=i, newsId=n, content=c) for i, n, c in rows]

    def delete_all_for_news(self, news_id: int) -> None:
        self._repo.delete_all_for_news(news_id)
