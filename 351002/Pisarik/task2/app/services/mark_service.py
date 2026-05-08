from typing import List, Optional

from sqlalchemy import select
from sqlalchemy.orm import Session

from app.dtos.mark_request import MarkRequestTo
from app.dtos.mark_response import MarkResponseTo
from app.db.orm import MarkOrm
from app.repositories.sqlalchemy_repository import PageParams, SqlAlchemyRepository

class MarkService:
    def __init__(self, db: Session) -> None:
        self._repo = SqlAlchemyRepository[MarkOrm](db, MarkOrm)
        self._db = db

    def create_mark(self, dto: MarkRequestTo) -> MarkResponseTo:
        created = self._repo.create(MarkOrm(name=dto.name))
        return MarkResponseTo(id=created.id, name=created.name)

    def get_mark(self, mark_id: int) -> Optional[MarkResponseTo]:
        entity = self._repo.get_by_id(mark_id)
        if not entity:
            return None
        return MarkResponseTo(id=entity.id, name=entity.name)

    def get_all_marks(self, page: PageParams, name: Optional[str] = None) -> List[MarkResponseTo]:
        stmt = select(MarkOrm)
        if name:
            stmt = stmt.where(MarkOrm.name.ilike(f"%{name}%"))
        items = self._repo.list(stmt, page)
        return [MarkResponseTo(id=i.id, name=i.name) for i in items]

    def update_mark(self, mark_id: int, dto: MarkRequestTo) -> Optional[MarkResponseTo]:
        existing = self._repo.get_by_id(mark_id)
        if not existing:
            return None
        existing.name = dto.name
        self._db.commit()
        self._db.refresh(existing)
        return MarkResponseTo(id=existing.id, name=existing.name)

    def delete_mark(self, mark_id: int) -> bool:
        return self._repo.delete(mark_id)
