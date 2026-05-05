from sqlalchemy.orm import Session
from sqlalchemy.exc import IntegrityError
from app.dto.mark import MarkRequestTo, MarkResponseTo
from app.models.mark import Mark
from app.core.exceptions import NotFoundException, AppException
from app.cache.redis_client import cache_get, cache_set, cache_delete


class MarkService:
    def __init__(self, db: Session):
        self.db = db

    def create(self, dto: MarkRequestTo) -> MarkResponseTo:
        mark = Mark(name=dto.name)
        try:
            self.db.add(mark)
            self.db.commit()
            self.db.refresh(mark)
        except IntegrityError:
            self.db.rollback()
            raise AppException(
                "Mark with this name already exists", 40303, 403)
        result = self._to_response(mark)
        cache_set(f"mark:{mark.id}", result.model_dump())
        cache_delete("marks:all")
        return result

    def find_all(self):
        cached = cache_get("marks:all")
        if cached is not None:
            return [MarkResponseTo(**m) for m in cached]
        marks = self.db.query(Mark).all()
        result = [self._to_response(m) for m in marks]
        cache_set("marks:all", [r.model_dump() for r in result])
        return result

    def find_by_id(self, id: int):
        cached = cache_get(f"mark:{id}")
        if cached is not None:
            return MarkResponseTo(**cached)
        mark = self.db.query(Mark).filter(Mark.id == id).first()
        if not mark:
            raise NotFoundException("Mark not found", 40403)
        result = self._to_response(mark)
        cache_set(f"mark:{id}", result.model_dump())
        return result

    def update(self, dto: MarkRequestTo):
        mark = self.db.query(Mark).filter(Mark.id == dto.id).first()
        if not mark:
            raise NotFoundException("Mark not found", 40403)

        mark.name = dto.name
        try:
            self.db.commit()
            self.db.refresh(mark)
        except IntegrityError:
            self.db.rollback()
            raise AppException(
                "Mark with this name already exists", 40303, 403)

        result = self._to_response(mark)
        cache_set(f"mark:{mark.id}", result.model_dump())
        cache_delete("marks:all")
        return result

    def delete(self, id: int):
        mark = self.db.query(Mark).filter(Mark.id == id).first()
        if not mark:
            raise NotFoundException("Mark not found", 40403)
        self.db.delete(mark)
        self.db.commit()
        cache_delete(f"mark:{id}")
        cache_delete("marks:all")

    def _to_response(self, mark: Mark) -> MarkResponseTo:
        return MarkResponseTo(id=mark.id, name=mark.name)
