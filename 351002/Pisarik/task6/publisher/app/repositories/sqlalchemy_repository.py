from __future__ import annotations

from dataclasses import dataclass
from typing import Any, Generic, Optional, TypeVar

from sqlalchemy import asc, desc, func, select
from sqlalchemy.orm import Session

TOrm = TypeVar("TOrm")


@dataclass(frozen=True)
class PageParams:
    page: int = 0
    size: int = 20
    sort: str = "id,asc"  # "field,asc|desc"


def _apply_sort(stmt: Any, model: Any, sort: str) -> Any:
    field, _, direction = sort.partition(",")
    field = field.strip() or "id"
    direction = (direction.strip() or "asc").lower()
    col = getattr(model, field, None)
    if col is None:
        col = getattr(model, "id")
    return stmt.order_by(desc(col) if direction == "desc" else asc(col))


class SqlAlchemyRepository(Generic[TOrm]):
    def __init__(self, db: Session, model: type[TOrm]) -> None:
        self.db = db
        self.model = model

    def create(self, entity: TOrm) -> TOrm:
        self.db.add(entity)
        self.db.commit()
        self.db.refresh(entity)
        return entity

    def get_by_id(self, entity_id: int) -> Optional[TOrm]:
        return self.db.get(self.model, entity_id)

    def delete(self, entity_id: int) -> bool:
        obj = self.get_by_id(entity_id)
        if not obj:
            return False
        self.db.delete(obj)
        self.db.commit()
        return True

    def count(self, stmt: Any) -> int:
        return int(self.db.execute(select(func.count()).select_from(stmt.subquery())).scalar_one())

    def list(self, stmt: Any, page: PageParams) -> list[TOrm]:
        stmt = _apply_sort(stmt, self.model, page.sort)
        stmt = stmt.offset(page.page * page.size).limit(page.size)
        return list(self.db.execute(stmt).scalars().all())

