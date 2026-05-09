from typing import TypeVar, Generic, Type, Optional, List, Any
from sqlalchemy.orm import Session
from sqlalchemy import asc, desc
from app.database import Base

ModelType = TypeVar("ModelType", bound=Base)


class BaseRepository(Generic[ModelType]):
    def __init__(self, model: Type[ModelType], db: Session):
        self.model = model
        self.db = db

    def get_by_id(self, entity_id: int) -> Optional[ModelType]:
        return self.db.query(self.model).filter(self.model.id == entity_id).first()

    def get_all(
        self,
        page: int = 0,
        size: int = 10,
        sort_by: str = "id",
        sort_order: str = "asc",
        filters: Optional[dict] = None
    ) -> List[ModelType]:
        query = self.db.query(self.model)

        if filters:
            for field, value in filters.items():
                if hasattr(self.model, field) and value is not None:
                    query = query.filter(getattr(self.model, field) == value)

        column = getattr(self.model, sort_by, self.model.id)
        if sort_order == "desc":
            query = query.order_by(desc(column))
        else:
            query = query.order_by(asc(column))

        return query.offset(page * size).limit(size).all()

    def create(self, obj: ModelType) -> ModelType:
        self.db.add(obj)
        self.db.commit()
        self.db.refresh(obj)
        return obj

    def update(self, obj: ModelType) -> ModelType:
        self.db.commit()
        self.db.refresh(obj)
        return obj

    def delete(self, obj: ModelType) -> None:
        self.db.delete(obj)
        self.db.commit()

    def get_by_field(self, field: str, value: Any) -> Optional[ModelType]:
        if hasattr(self.model, field):
            return self.db.query(self.model).filter(
                getattr(self.model, field) == value
            ).first()
        return None
