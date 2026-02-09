from app.repositories.base_repository import InMemoryRepository
from app.models.marker import Marker
from app.dtos.marker_dto import MarkerRequestTo, MarkerResponseTo
from app.errors import AppError
from typing import List

marker_repository = InMemoryRepository[Marker]()


class MarkerService:
    def create(self, dto: MarkerRequestTo) -> MarkerResponseTo:
        existing = marker_repository.find_by_condition(lambda m: m.name == dto.name)
        if existing:
            raise AppError(status_code=403, message="Marker name already exists", error_code=40307)

        entity = Marker(name=dto.name)
        saved_entity = marker_repository.create(entity)

        # ИСПРАВЛЕНИЕ ЗДЕСЬ
        return MarkerResponseTo(id=saved_entity.id, name=saved_entity.name)

    def get_all(self) -> List[MarkerResponseTo]:
        entities = marker_repository.get_all()
        # ИСПРАВЛЕНИЕ ЗДЕСЬ
        return [MarkerResponseTo(id=e.id, name=e.name) for e in entities]

    def get_by_id(self, id: int) -> MarkerResponseTo:
        entity = marker_repository.get_by_id(id)
        if not entity:
            raise AppError(status_code=404, message="Marker not found", error_code=40407)
        # ИСПРАВЛЕНИЕ ЗДЕСЬ
        return MarkerResponseTo(id=entity.id, name=entity.name)

    def update(self, id: int, dto: MarkerRequestTo) -> MarkerResponseTo:
        entity = marker_repository.get_by_id(id)
        if not entity:
            raise AppError(status_code=404, message="Marker not found", error_code=40408)

        existing = marker_repository.find_by_condition(
            lambda m: m.name == dto.name and m.id != id
        )
        if existing:
            raise AppError(status_code=403, message="Marker name already exists", error_code=40308)

        updated_entity = Marker(id=id, name=dto.name)
        marker_repository.update(id, updated_entity)
        # ИСПРАВЛЕНИЕ ЗДЕСЬ
        return MarkerResponseTo(id=updated_entity.id, name=updated_entity.name)

    def delete(self, id: int) -> None:
        entity = marker_repository.get_by_id(id)
        if not entity:
            raise AppError(status_code=404, message="Marker not found", error_code=40409)
        marker_repository.delete(id)
