from typing import Dict, Optional, List
from threading import Lock
from publisher.app.core.writers.model import Writer

class InMemoryWriterRepo:
    def __init__(self):
        self._data: Dict[int, Writer] = {}
        self._next_id = 1
        self._lock = Lock()

    def create(self, writer: Writer) -> Writer:
        with self._lock:
            writer.id = self._next_id
            self._data[writer.id] = writer
            self._next_id += 1
            return writer

    def get_by_id(self, id: int) -> Optional[Writer]:
        return self._data.get(id)

    def get_by_login(self, login: str) -> Optional[Writer]:
        for w in self._data.values():
            if w.login == login:
                return w
        return None

    def update(self, id: int, new_writer: Writer) -> Writer:
        with self._lock:
            if id not in self._data:
                raise KeyError("not found")
            existing = self._data[id]
            existing.login = new_writer.login
            existing.password = new_writer.password
            existing.firstname = new_writer.firstname
            existing.lastname = new_writer.lastname
            return existing

    def delete(self, id: int) -> None:
        with self._lock:
            if id in self._data:
                del self._data[id]
            else:
                raise KeyError("not found")

    def list_all(self) -> List[Writer]:
        return list(self._data.values())