from typing import List

from src.core.constants import ErrorStatus
from src.core.errors import HttpNotFoundError, NoteErrorMessage
from src.domain.models import Note
from src.domain.repositories.interfaces import Repository
from src.schemas.note import NoteResponseTo, NoteRequestTo


class NoteService:
    def __init__(self, repo: Repository[Note]):
        self._repo = repo

    def get_one(self, note_id: int) -> NoteResponseTo:
        try:
            note = self._repo.get_one(note_id)
        except KeyError:
            raise HttpNotFoundError(NoteErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)
        return NoteResponseTo.model_validate(note)
    
    def get_all(self) -> List[Note]:
        return self._repo.get_all()
    
    def create(self, dto: NoteRequestTo) -> NoteResponseTo:
        note = Note(
            id=0,
            content=dto.content,
            topic_id=dto.topic_id
        )
        created_note = self._repo.create(note)
        return NoteResponseTo.model_validate(created_note)

    def update(self, note_id: int, dto: NoteRequestTo) -> NoteResponseTo:
        old_note = Note(
            id=note_id,
            content=dto.content,
            topic_id=dto.topic_id
        )
        try:
            updated_note = self._repo.update(old_note)
        except KeyError:
            raise HttpNotFoundError(NoteErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)
        return NoteResponseTo.model_validate(updated_note)

    def delete(self, note_id: int) -> None:
        try:
            self._repo.delete(note_id)
        except KeyError:
            raise HttpNotFoundError(NoteErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)