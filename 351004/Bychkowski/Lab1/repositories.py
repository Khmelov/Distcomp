from typing import TypeVar, Generic, Type, Optional, List
from sqlalchemy.orm import Session
from database import Base

T = TypeVar('T', bound=Base)

class PostgresRepository(Generic[T]):
    def __init__(self, model: Type[T], db: Session):
        self.model = model
        self.db = db

    def save(self, entity: T) -> T:
        self.db.add(entity)
        self.db.commit()
        self.db.refresh(entity)
        return entity

    def find_by_id(self, id: int) -> Optional[T]:
        return self.db.query(self.model).filter(self.model.id == id).first()

    def find_all(self) -> List[T]:
        return self.db.query(self.model).all()

    def update(self, entity: T) -> T:
        self.db.commit()
        self.db.refresh(entity)
        return entity

    def delete(self, id: int) -> bool:
        entity = self.find_by_id(id)
        if entity:
            self.db.delete(entity)
            self.db.commit()
            return True
        return False