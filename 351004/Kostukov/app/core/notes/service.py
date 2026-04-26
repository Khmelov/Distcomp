from typing import List, Optional
from datetime import datetime
from app.core.notes.model import Note
from app.core.notes.dto import NoteRequestTo, NoteResponseTo
from app.core.notes.repo import InMemoryNoteRepo
from app.core.exceptions import AppError

class NoteService:
    def __init__(self, repo: InMemoryNoteRepo, article_repo: Optional[object] = None):
        self.repo = repo
        self.article_repo = article_repo

    def _validate_article_exists(self, article_id: int):
        if not self.article_repo:
            raise AppError(status_code=500, message="Article repository is not configured", suffix=20)
        article = None
        try:
            article = self.article_repo.get_by_id(article_id)
        except Exception:
            article = None
        if not article:
            raise AppError(status_code=400, message=f"Article with id {article_id} does not exist", suffix=21)

    def create_note(self, dto: NoteRequestTo) -> NoteResponseTo:
        model = Note(
            id=0,
            article_id=dto.article_id,
            content=dto.content,
            created_at=datetime.utcnow()
        )
        created = self.repo.create(model)
        return NoteResponseTo(
            id=created.id,
            articleId=created.article_id,
            content=created.content,
            createdAt=created.created_at
        )

    def get_by_id(self, id: int) -> NoteResponseTo:
        n = self.repo.get_by_id(id)
        if not n:
            raise AppError(status_code=404, message="Note not found", suffix=22)
        return NoteResponseTo(id=n.id, articleId=n.article_id, content=n.content, createdAt=n.created_at)

    def list_notes(self) -> List[NoteResponseTo]:
        notes = self.repo.list_all()
        return [NoteResponseTo(id=n.id, articleId=n.article_id, content=n.content, createdAt=n.created_at)
                for n in notes]

    def list_by_article_id(self, article_id: int) -> List[NoteResponseTo]:
        self._validate_article_exists(article_id)
        notes = self.repo.list_by_article_id(article_id)
        return [NoteResponseTo(id=n.id, articleId=n.article_id, content=n.content, createdAt=n.created_at)
                for n in notes]

    def update_note(self, id: int, dto: NoteRequestTo) -> NoteResponseTo:
        existing = self.repo.get_by_id(id)
        if not existing:
            raise AppError(status_code=404, message="Note not found", suffix=23)

        if dto.article_id != existing.article_id:
            self._validate_article_exists(dto.article_id)

        existing.article_id = dto.article_id
        existing.content = dto.content
        updated = self.repo.update(id, existing)
        return NoteResponseTo(id=updated.id, articleId=updated.article_id, content=updated.content, createdAt=updated.created_at)

    def delete_note(self, id: int) -> None:
        existing = self.repo.get_by_id(id)
        if not existing:
            raise AppError(status_code=404, message="Note not found", suffix=24)
        try:
            self.repo.delete(id)
        except KeyError:
            raise AppError(status_code=404, message="Note not found", suffix=25)
        return None
