from typing import List, Optional

from sqlalchemy import select
from sqlalchemy.orm import Session

from app.dtos.mark_request import MarkRequestTo
from app.dtos.mark_response import MarkResponseTo
from app.db.orm import MarkOrm
from app.redis_cache import cache_get_json, cache_set_json, entity_id_key, entity_list_key, invalidate_entity
from app.repositories.sqlalchemy_repository import PageParams, SqlAlchemyRepository


class MarkService:
    def __init__(self, db: Session) -> None:
        self._repo = SqlAlchemyRepository[MarkOrm](db, MarkOrm)
        self._db = db

    def create_mark(self, dto: MarkRequestTo) -> MarkResponseTo:
        created = self._repo.create(MarkOrm(name=dto.name))
        invalidate_entity("mark")
        out = MarkResponseTo(id=created.id, name=created.name)
        cache_set_json(entity_id_key("mark", out.id), out.model_dump(mode="json"))
        return out

    def get_mark(self, mark_id: int) -> Optional[MarkResponseTo]:
        k = entity_id_key("mark", mark_id)
        cached = cache_get_json(k)
        if isinstance(cached, dict):
            try:
                return MarkResponseTo.model_validate(cached)
            except Exception:
                pass
        entity = self._repo.get_by_id(mark_id)
        if not entity:
            return None
        out = MarkResponseTo(id=entity.id, name=entity.name)
        cache_set_json(k, out.model_dump(mode="json"))
        return out

    def get_all_marks(self, page: PageParams, name: Optional[str] = None) -> List[MarkResponseTo]:
        params = {"page": page.page, "size": page.size, "sort": page.sort, "name": name or ""}
        lk = entity_list_key("mark", params)
        cached = cache_get_json(lk)
        if isinstance(cached, list):
            try:
                return [MarkResponseTo.model_validate(x) for x in cached]
            except Exception:
                pass
        stmt = select(MarkOrm)
        if name:
            stmt = stmt.where(MarkOrm.name.ilike(f"%{name}%"))
        items = self._repo.list(stmt, page)
        out = [MarkResponseTo(id=i.id, name=i.name) for i in items]
        cache_set_json(lk, [x.model_dump(mode="json") for x in out])
        return out

    def update_mark(self, mark_id: int, dto: MarkRequestTo) -> Optional[MarkResponseTo]:
        existing = self._repo.get_by_id(mark_id)
        if not existing:
            return None
        existing.name = dto.name
        self._db.commit()
        self._db.refresh(existing)
        invalidate_entity("mark", mark_id)
        out = MarkResponseTo(id=existing.id, name=existing.name)
        cache_set_json(entity_id_key("mark", out.id), out.model_dump(mode="json"))
        return out

    def delete_mark(self, mark_id: int) -> bool:
        ok = self._repo.delete(mark_id)
        if ok:
            invalidate_entity("mark", mark_id)
        return ok
