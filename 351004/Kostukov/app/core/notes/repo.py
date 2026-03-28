from threading import Lock
from typing import Dict, List, Optional, Callable
from app.core.notes.model import Note
from datetime import datetime

class InMemoryNoteRepo:
    def __init__(self):
        self._data: Dict[int, Note] = {}
        self._next_id = 1
        self._lock = Lock()

    def create(self, note: Note) -> Note:
        with self._lock:
            note.id = self._next_id
            if not isinstance(note.created_at, datetime):
                note.created_at = datetime.utcnow()
            self._data[note.id] = note
            self._next_id += 1
            return note

    def get_by_id(self, id: int) -> Optional[Note]:
        return self._data.get(id)

    def update(self, id: int, note: Note) -> Note:
        with self._lock:
            if id not in self._data:
                raise KeyError("not found")
            existing = self._data[id]
            existing.article_id = note.article_id
            existing.content = note.content
            self._data[id] = existing
            return existing

    def delete(self, id: int) -> None:
        with self._lock:
            if id in self._data:
                del self._data[id]
            else:
                raise KeyError("not found")

    def list_all(self) -> List[Note]:
        return list(self._data.values())

    def list_by_article_id(self, article_id: int) -> List[Note]:
        return [n for n in self._data.values() if n.article_id == article_id]

    def list_filtered(self, predicate: Callable[[Note], bool]) -> List[Note]:
        return [n for n in self._data.values() if predicate(n)]
