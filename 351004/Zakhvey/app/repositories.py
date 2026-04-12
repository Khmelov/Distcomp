from typing import TypeVar, Generic, List, Optional, Type, Any
from sqlalchemy.orm import Session
from sqlalchemy import asc, desc

T = TypeVar('T')


class SQLAlchemyRepository(Generic[T]):
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

    def find_all(self, skip: int = 0, limit: int = 100, sort_by: str = 'id', sort_order: str = 'asc', **filters) -> \
    List[T]:
        query = self.db.query(self.model)

        # Фильтрация
        for attr, value in filters.items():
            if hasattr(self.model, attr) and value is not None:
                query = query.filter(getattr(self.model, attr) == value)

        # Сортировка
        if hasattr(self.model, sort_by):
            order_column = getattr(self.model, sort_by)
            query = query.order_by(asc(order_column) if sort_order == 'asc' else desc(order_column))

        # Пагинация
        query = query.offset(skip).limit(limit)
        return query.all()

    def delete(self, id: int) -> bool:
        entity = self.find_by_id(id)
        if entity:
            self.db.delete(entity)
            self.db.commit()
            return True
        return False