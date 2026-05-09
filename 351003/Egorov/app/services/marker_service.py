from typing import List

from app.dtos.marker_request import MarkerRequestTo
from app.dtos.marker_response import MarkerResponseTo
from app.models.marker import Marker
from app.repositories.base_repository import BaseRepository


class MarkerService:
    def __init__(self, repository: BaseRepository[Marker]) -> None:
        self._repository = repository

    def create_marker(self, dto: MarkerRequestTo) -> MarkerResponseTo:
        entity = Marker(name=dto.name)
        created = self._repository.create(entity)
        return self._to_response(created)

    def get_marker(self, marker_id: int) -> MarkerResponseTo | None:
        entity = self._repository.read_by_id(marker_id)
        return self._to_response(entity) if entity else None

    def get_all_markers(self) -> List[MarkerResponseTo]:
        return [self._to_response(m) for m in self._repository.read_all()]

    def update_marker(self, marker_id: int, dto: MarkerRequestTo) -> MarkerResponseTo | None:
        if self._repository.read_by_id(marker_id) is None:
            return None
        entity = Marker(id=marker_id, name=dto.name)
        updated = self._repository.update(marker_id, entity)
        return self._to_response(updated)

    def delete_marker(self, marker_id: int) -> bool:
        if self._repository.read_by_id(marker_id) is None:
            return False
        self._repository.delete(marker_id)
        return True

    @staticmethod
    def _to_response(entity: Marker) -> MarkerResponseTo:
        return MarkerResponseTo(
            id=entity.id or 0,
            name=entity.name,
            links={"self": f"/api/v1.0/markers/{entity.id}"},
        )

