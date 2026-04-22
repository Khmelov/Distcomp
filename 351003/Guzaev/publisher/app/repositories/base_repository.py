from sqlalchemy.orm import Session
from typing import Generic, TypeVar, List, Optional

T = TypeVar('T')

class BaseRepository(Generic[T]):
    def __init__(self, model: T):
        self.model = model

    def create(self, db: Session, entity: T) -> T:
        db.add(entity)
        db.commit()
        db.refresh(entity)
        return entity

    def get_all(self, db: Session) -> List[T]:
        return db.query(self.model).all()

    def get_by_id(self, db: Session, id: int) -> Optional[T]:
        return db.query(self.model).filter(self.model.id == id).first()

    def delete(self, db: Session, id: int) -> bool:
        entity = self.get_by_id(db, id)
        if entity:
            db.delete(entity)
            db.commit()
            return True
        return False
