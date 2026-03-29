from repositories.base_repository import BaseRepository
from models.models import Marker
from dtos.marker_dto import MarkerRequestTo, MarkerResponseTo
from database import SessionLocal
from errors import AppError
from typing import List

marker_repo = BaseRepository[Marker](Marker)


class MarkerService:
    def create(self, dto: MarkerRequestTo) -> MarkerResponseTo:
        with SessionLocal() as db:
            existing = db.query(Marker).filter(Marker.name == dto.name).first()
            if existing:
                raise AppError(status_code=403, message="Marker name already exists", error_code=40307)

            entity = Marker(name=dto.name)
            saved = marker_repo.create(db, entity)
            return MarkerResponseTo(id=saved.id, name=saved.name)

    def get_all(self) -> List[MarkerResponseTo]:
        with SessionLocal() as db:
            entities = marker_repo.get_all(db)
            return [MarkerResponseTo(id=e.id, name=e.name) for e in entities]

    def get_by_id(self, id: int) -> MarkerResponseTo:
        with SessionLocal() as db:
            entity = marker_repo.get_by_id(db, id)
            if not entity:
                raise AppError(status_code=404, message="Marker not found", error_code=40407)
            return MarkerResponseTo(id=entity.id, name=entity.name)

    def update(self, id: int, dto: MarkerRequestTo) -> MarkerResponseTo:
        with SessionLocal() as db:
            entity = marker_repo.get_by_id(db, id)
            if not entity:
                raise AppError(status_code=404, message="Marker not found", error_code=40408)

            existing = db.query(Marker).filter(Marker.name == dto.name, Marker.id != id).first()
            if existing:
                raise AppError(status_code=403, message="Marker name already exists", error_code=40308)

            entity.name = dto.name
            db.commit()
            db.refresh(entity)
            return MarkerResponseTo(id=entity.id, name=entity.name)

    def delete(self, id: int) -> None:
        with SessionLocal() as db:
            success = marker_repo.delete(db, id)
            if not success:
                raise AppError(status_code=404, message="Marker not found", error_code=40409)
