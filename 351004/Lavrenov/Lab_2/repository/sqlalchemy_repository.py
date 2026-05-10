from typing import List, Optional, TypeVar, Generic, Type, Set
from sqlalchemy.orm import Session
from repository.interface import IRepository

EntityT = TypeVar("EntityT")
ModelT = TypeVar("ModelT")


class SQLAlchemyRepository(IRepository[EntityT], Generic[EntityT, ModelT]):
    _immutable_fields: Set[str] = {"id", "created", "modified"}

    def __init__(self, session: Session, model_cls: Type[ModelT]):
        self._session = session
        self._model_cls = model_cls

    def get(self, id: int) -> Optional[EntityT]:
        db_obj = (
            self._session.query(self._model_cls)
            .filter(self._model_cls.id == id)
            .first()
        )
        if db_obj:
            return self._to_entity(db_obj)
        return None

    def get_all(self) -> List[EntityT]:
        db_objs = self._session.query(self._model_cls).all()
        return [self._to_entity(obj) for obj in db_objs]

    def create(self, entity: EntityT) -> EntityT:
        db_obj = self._to_model(entity)
        self._session.add(db_obj)
        self._session.commit()
        self._session.refresh(db_obj)
        return self._to_entity(db_obj)

    def update(self, entity: EntityT) -> EntityT:
        db_obj = (
            self._session.query(self._model_cls)
            .filter(self._model_cls.id == entity.id)
            .first()
        )
        if not db_obj:
            raise ValueError(f"Entity with id {entity.id} not found")
        entity_dict = self._entity_to_dict(entity)
        for key, value in entity_dict.items():
            if key not in self._immutable_fields:
                setattr(db_obj, key, value)
        self._session.commit()
        self._session.refresh(db_obj)
        return self._to_entity(db_obj)

    def delete(self, id: int) -> bool:
        db_obj = (
            self._session.query(self._model_cls)
            .filter(self._model_cls.id == id)
            .first()
        )
        if db_obj:
            self._session.delete(db_obj)
            self._session.commit()
            return True
        return False

    def find_all(
        self, filters: dict, offset: int, limit: int, sort_by: str, order: str
    ) -> List[EntityT]:
        query = self._session.query(self._model_cls)
        query = self._apply_filters(query, filters)
        sort_column = getattr(self._model_cls, sort_by, None)
        if sort_column is not None:
            if order.lower() == "desc":
                query = query.order_by(sort_column.desc())
            else:
                query = query.order_by(sort_column.asc())
        query = query.offset(offset).limit(limit)
        db_objs = query.all()
        return [self._to_entity(obj) for obj in db_objs]

    def count(self, filters: dict) -> int:
        query = self._session.query(self._model_cls)
        query = self._apply_filters(query, filters)
        return query.count()

    def _apply_filters(self, query, filters: dict):
        for attr, value in filters.items():
            column = getattr(self._model_cls, attr, None)
            if column is not None:
                query = query.filter(column == value)
        return query

    def _to_entity(self, db_obj: ModelT) -> EntityT:
        raise NotImplementedError

    def _to_model(self, entity: EntityT) -> ModelT:
        raise NotImplementedError

    def _entity_to_dict(self, entity: EntityT) -> dict:
        # Преобразует датакласс в словарь (работает для всех полей)
        return entity.__dict__
