from sqlalchemy.orm import Session
import models

class BaseRepository:
    def __init__(self, model):
        self.model = model

    def get_all(self, db: Session, skip: int = 0, limit: int = 10, sort_by: str = "id", **filters):
        query = db.query(self.model)
        for attr, value in filters.items():
            if value is not None and hasattr(self.model, attr):
                column = getattr(self.model, attr)
                query = query.filter(column.ilike(f"%{value}%") if isinstance(value, str) else column == value)
        return query.order_by(getattr(self.model, sort_by)).offset(skip).limit(limit).all()

    def get_by_id(self, db: Session, obj_id: int):
        return db.query(self.model).filter(self.model.id == obj_id).first()

    def create(self, db: Session, data: dict):
        obj = self.model(**data)
        db.add(obj)
        db.commit()
        db.refresh(obj)
        return obj

    def update(self, db: Session, obj_id: int, data: dict):
        obj = self.get_by_id(db, obj_id)
        if obj:
            for key, val in data.items(): setattr(obj, key, val)
            db.commit()
            db.refresh(obj)
        return obj

    def delete(self, db: Session, obj_id: int):
        obj = self.get_by_id(db, obj_id)
        if obj:
            db.delete(obj)
            db.commit()
            return True
        return False

class AuthorRepo(BaseRepository):
    def __init__(self): super().__init__(models.Author)
class IssueRepo(BaseRepository):
    def __init__(self): super().__init__(models.Issue)
class NoteRepo(BaseRepository):
    def __init__(self): super().__init__(models.Note)
class StickerRepo(BaseRepository):
    def __init__(self): super().__init__(models.Sticker)