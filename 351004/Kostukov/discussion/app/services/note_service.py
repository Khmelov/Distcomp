from fastapi import HTTPException

from discussion.app.infrastructure.cassandra.repo import CassandraNoteRepository
from discussion.app.schemas import NoteRequestTo


class NoteService:
    def __init__(self, repo: CassandraNoteRepository):
        self.repo = repo

    def create(self, dto: NoteRequestTo) -> dict:
        data = dto.model_dump(exclude_none=True, by_alias=False)
        return self.repo.create(data)

    def get_all(self, skip: int = 0, limit: int = 10) -> list[dict]:
        return self.repo.get_all(skip=skip, limit=limit)

    def get_by_id(self, note_id: int) -> dict:
        item = self.repo.get_by_id(note_id)
        if item is None:
            raise HTTPException(status_code=404, detail="Note not found")
        return item

    def get_by_article_id(self, article_id: int) -> list[dict]:
        return self.repo.get_by_article_id(article_id)

    def update(self, note_id: int, dto: NoteRequestTo) -> dict:
        data = dto.model_dump(exclude_none=True, by_alias=False)
        item = self.repo.update(note_id, data)
        if item is None:
            raise HTTPException(status_code=404, detail="Note not found")
        return item

    def delete(self, note_id: int) -> None:
        deleted = self.repo.delete(note_id)
        if not deleted:
            raise HTTPException(status_code=404, detail="Note not found")