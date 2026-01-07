from typing import Optional
from app.models.marker import Marker
from app.schemas.marker import MarkerCreate, MarkerUpdate
from app.repositories.base_repository import BaseRepository

class MarkerRepository(BaseRepository[Marker]):
    def __init__(self):
        self._markers = []
        self._id_counter = 1

    def add(self, marker_data: MarkerCreate) -> Marker:
        marker = Marker(id=self._id_counter, name=marker_data.name)
        self._id_counter += 1
        self._markers.append(marker)
        return marker

    def get_by_id(self, marker_id: int) -> Optional[Marker]:
        return next((m for m in self._markers if m.id == marker_id), None)

    def list(self) -> list[Marker]:
        return self._markers.copy()

    def delete(self, marker_id: int) -> bool:
        marker = self.get_by_id(marker_id)
        if marker:
            self._markers.remove(marker)
            return True
        return False

    def update(self, marker_data: MarkerUpdate) -> Optional[Marker]:
        marker = self.get_by_id(marker_data.id)
        if marker:
            marker.name = marker_data.name
            return marker
        return None
