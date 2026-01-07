from typing import List, Optional
from app.schemas.note import NoteCreate
from app.models.note import Note
from app.repositories.note_repository import NoteRepository

class NoteService:
    def __init__(self, repo: NoteRepository):
        self.repo = repo

    def create_note(self, note_create: NoteCreate) -> Note:
        return self.repo.add(note_create)

    def get_note(self, note_id: int) -> Optional[Note]:
        return self.repo.get_by_id(note_id)

    def list_notes(self) -> List[Note]:
        return self.repo.list_notes()

    def delete_note(self, note_id: int) -> bool:
        return self.repo.delete(note_id)

    def update_note(self, note: Note) -> Optional[Note]:
        return self.repo.update(note)
