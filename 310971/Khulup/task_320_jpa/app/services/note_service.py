from typing import List, Optional
from app.schemas.note import NoteCreate, NoteUpdate
from app.models.note import Note
from app.repositories.note_repository import NoteRepository

class NoteService:
    def __init__(self, repo: NoteRepository):
        self.repo = repo

    def create_note(self, note_create: NoteCreate) -> Note:
        try:
            return self.repo.add(note_create)
        except ValueError as e:
            if "does not exist" in str(e):
                raise ValueError("Issue with this id does not exist")
            raise

    def get_note(self, note_id: int) -> Optional[Note]:
        return self.repo.get_by_id(note_id)

    def list_notes(
        self,
        issueId: int | None = None,
        limit: int = 50,
        offset: int = 0,
        sort_by: str | None = None,
        sort_dir: str = "desc",
    ) -> List[Note]:
        return self.repo.list_notes(
            issueId=issueId, limit=limit, offset=offset, sort_by=sort_by, sort_dir=sort_dir
        )

    def delete_note(self, note_id: int) -> bool:
        return self.repo.delete(note_id)

    def update_note(self, note: NoteUpdate) -> Optional[Note]:
        return self.repo.update(note)
