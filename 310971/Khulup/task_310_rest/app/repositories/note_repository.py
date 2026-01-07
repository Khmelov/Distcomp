from typing import Optional, List
from app.models.note import Note
from app.schemas.note import NoteCreate
from app.repositories.base_repository import BaseRepository

class NoteRepository(BaseRepository[Note]):
    def __init__(self):
        self._notes: List[Note] = []
        self._id_counter = 1

    def add(self, note_create: NoteCreate) -> Note:
        note = Note(
            id=self._id_counter,
            issueId=note_create.issueId,
            content=note_create.content
        )
        self._id_counter += 1
        self._notes.append(note)
        return note

    def get_by_id(self, note_id: int) -> Optional[Note]:
        return next((n for n in self._notes if n.id == note_id), None)

    def list_notes(self) -> List[Note]:
        return self._notes.copy()

    def list(self) -> List[Note]:
        return self._notes.copy()

    def delete(self, note_id: int) -> bool:
        note = self.get_by_id(note_id)
        if note:
            self._notes.remove(note)
            return True
        return False

    def update(self, updated_note: Note) -> Optional[Note]:
        existing = self.get_by_id(updated_note.id)
        if not existing:
            return None
        existing.issueId = updated_note.issueId
        existing.content = updated_note.content
        return existing
