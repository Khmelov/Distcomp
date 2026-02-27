from typing import List
from sqlalchemy.orm import Session
from app.models import Marker
from app.repository import BaseRepository
from app.schemas.marker import MarkerCreate, MarkerUpdate, MarkerResponse
from app.core.exceptions import EntityNotFoundException, EntityAlreadyExistsException


class MarkerService:
    def __init__(self, db: Session):
        self.repo = BaseRepository(Marker, db)

    def get_all(
        self,
        page: int = 0,
        size: int = 10,
        sort_by: str = "id",
        sort_order: str = "asc"
    ) -> List[MarkerResponse]:
        markers = self.repo.get_all(page=page, size=size, sort_by=sort_by, sort_order=sort_order)
        return [MarkerResponse.model_validate(m) for m in markers]

    def get_by_id(self, marker_id: int) -> MarkerResponse:
        marker = self.repo.get_by_id(marker_id)
        if not marker:
            raise EntityNotFoundException("Marker", marker_id)
        return MarkerResponse.model_validate(marker)

    def create(self, data: MarkerCreate) -> MarkerResponse:
        existing = self.repo.get_by_field("name", data.name)
        if existing:
            raise EntityAlreadyExistsException("Marker", "name", data.name)
        marker = Marker(name=data.name)
        created = self.repo.create(marker)
        return MarkerResponse.model_validate(created)

    def update(self, data: MarkerUpdate) -> MarkerResponse:
        marker = self.repo.get_by_id(data.id)
        if not marker:
            raise EntityNotFoundException("Marker", data.id)
        existing = self.repo.get_by_field("name", data.name)
        if existing and existing.id != data.id:
            raise EntityAlreadyExistsException("Marker", "name", data.name)
        marker.name = data.name
        updated = self.repo.update(marker)
        return MarkerResponse.model_validate(updated)

    def delete(self, marker_id: int) -> None:
        marker = self.repo.get_by_id(marker_id)
        if not marker:
            raise EntityNotFoundException("Marker", marker_id)
        self.repo.delete(marker)
