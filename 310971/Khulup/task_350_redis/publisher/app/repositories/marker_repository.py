from typing import Optional
from app.models.marker import Marker
from app.schemas.marker import MarkerCreate, MarkerUpdate
from app.repositories.base_repository import BaseRepository
from app.models.entities import MarkerEntity
from app.core.db import SessionLocal
from sqlalchemy.orm import Session

class MarkerRepository(BaseRepository[Marker]):
    def __init__(self, session: Optional[Session] = None):
        self.session = session or SessionLocal()

    def add(self, marker_data: MarkerCreate) -> Marker:
        marker_entity = MarkerEntity(name=marker_data.name)
        self.session.add(marker_entity)
        self.session.commit()
        self.session.refresh(marker_entity)
        return Marker.model_validate(marker_entity)

    def get_by_id(self, marker_id: int) -> Optional[Marker]:
        marker_entity = self.session.query(MarkerEntity).filter(MarkerEntity.id == marker_id).first()
        return Marker.model_validate(marker_entity) if marker_entity else None

    def list(self, name: str | None = None, limit: int = 50, offset: int = 0,
             sort_by: str | None = None, sort_dir: str = "desc") -> list[Marker]:
        query = self.session.query(MarkerEntity)
        
        if name:
            query = query.filter(MarkerEntity.name.contains(name))
        
        if sort_by:
            sort_col = getattr(MarkerEntity, sort_by, MarkerEntity.id)
            if sort_dir == "desc":
                query = query.order_by(sort_col.desc())
            else:
                query = query.order_by(sort_col.asc())
        else:
            query = query.order_by(MarkerEntity.id.desc())
        
        query = query.offset(offset).limit(limit)
        markers = query.all()
        return [Marker.model_validate(m) for m in markers]

    def delete(self, marker_id: int) -> bool:
        marker_entity = self.session.query(MarkerEntity).filter(MarkerEntity.id == marker_id).first()
        if marker_entity:
            self.session.delete(marker_entity)
            self.session.commit()
            return True
        return False

    def update(self, marker_data: MarkerUpdate) -> Optional[Marker]:
        marker_entity = self.session.query(MarkerEntity).filter(MarkerEntity.id == marker_data.id).first()
        if marker_entity:
            marker_entity.name = marker_data.name
            self.session.commit()
            self.session.refresh(marker_entity)
            return Marker.model_validate(marker_entity)
        return None
