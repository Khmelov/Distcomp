from threading import Lock
from typing import Dict, List, Optional
from publisher.app.core.markers.model import Marker

class InMemoryMarkerRepo:
    def __init__(self):
        self._data: Dict[int, Marker] = {}
        self._next_id = 1
        self._lock = Lock()

    def create(self, marker: Marker) -> Marker:
        with self._lock:
            marker.id = self._next_id
            self._data[marker.id] = marker
            self._next_id += 1
            return marker

    def get_by_id(self, id: int) -> Optional[Marker]:
        return self._data.get(id)

    def get_by_name(self, name: str) -> Optional[Marker]:
        for m in self._data.values():
            if m.name == name:
                return m
        return None

    def update(self, id: int, marker: Marker) -> Marker:
        with self._lock:
            if id not in self._data:
                raise KeyError("not found")
            existing = self._data[id]
            existing.name = marker.name
            return existing

    def delete(self, id: int) -> None:
        with self._lock:
            if id in self._data:
                del self._data[id]
            else:
                raise KeyError("not found")

    def list_all(self) -> List[Marker]:
        return list(self._data.values())

    def find_ids_by_names(self, names: List[str]) -> List[int]:
        result = []
        for name in names:
            m = self.get_by_name(name)
            if m:
                result.append(m.id)
        return result
