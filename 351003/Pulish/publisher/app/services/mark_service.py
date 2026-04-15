from sqlalchemy.orm import Session
from sqlalchemy.exc import IntegrityError
from app.dto.mark import MarkRequestTo, MarkResponseTo
from app.models.mark import Mark
from app.core.exceptions import NotFoundException, AppException


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
        return self._to_response(mark)

    def find_all(self):
        marks = self.db.query(Mark).all()
        return [self._to_response(m) for m in marks]

    def find_by_id(self, id: int):
        mark = self.db.query(Mark).filter(Mark.id == id).first()
        if not mark:
            raise NotFoundException("Mark not found", 40403)
        return self._to_response(mark)

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

        return self._to_response(mark)

    def delete(self, id: int):
        mark = self.db.query(Mark).filter(Mark.id == id).first()
        if not mark:
            raise NotFoundException("Mark not found", 40403)
        self.db.delete(mark)
        self.db.commit()

    def _to_response(self, mark: Mark) -> MarkResponseTo:
        return MarkResponseTo(id=mark.id, name=mark.name)
