from app.repositories.marker_repository import MarkerRepository
from app.schemas.marker import MarkerCreate, MarkerUpdate
from app.models.marker import Marker
from typing import Optional

class MarkerService:
    def __init__(self, repo: MarkerRepository):
        self.repo = repo

    def create(self, data: MarkerCreate) -> Marker:
        return self.repo.add(data)

    def get(self, marker_id: int) -> Optional[Marker]:
        return self.repo.get_by_id(marker_id)

    def list(self) -> list[Marker]:
        return self.repo.list()

    def delete(self, marker_id: int) -> bool:
        return self.repo.delete(marker_id)

    def update(self, data: MarkerUpdate) -> Optional[Marker]:
        return self.repo.update(data)
