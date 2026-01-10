from __future__ import annotations

from typing import Dict, Generic, Iterable, List, Optional, TypeVar

from app.exceptions import AppError, make_error_code

T = TypeVar("T")


class BaseRepository(Generic[T]):
    def create(self, entity: T) -> T:
        raise NotImplementedError

    def get(self, entity_id: int) -> Optional[T]:
        raise NotImplementedError

    def update(self, entity_id: int, entity: T) -> T:
        raise NotImplementedError

    def delete(self, entity_id: int) -> None:
        raise NotImplementedError

    def list_all(self) -> Iterable[T]:
        raise NotImplementedError


class InMemoryRepository(BaseRepository[T]):
    def __init__(self) -> None:
        self._store: Dict[int, T] = {}
        self._next_id = 1

    def _generate_id(self) -> int:
        current = self._next_id
        self._next_id += 1
        return current

    def create(self, entity: T) -> T:
        entity_id = getattr(entity, "id", None)
        if entity_id is None or entity_id == 0:
            entity_id = self._generate_id()
            setattr(entity, "id", entity_id)
        elif entity_id in self._store:
            raise AppError(
                status_code=409,
                error_message="Entity with given id already exists",
                error_code=make_error_code(409, 1),
            )
        self._store[entity_id] = entity
        return entity

    def get(self, entity_id: int) -> Optional[T]:
        return self._store.get(entity_id)

    def update(self, entity_id: int, entity: T) -> T:
        if entity_id not in self._store:
            raise AppError(
                status_code=404,
                error_message="Entity not found",
                error_code=make_error_code(404, 1),
            )
        setattr(entity, "id", entity_id)
        self._store[entity_id] = entity
        return entity

    def delete(self, entity_id: int) -> None:
        if entity_id not in self._store:
            raise AppError(
                status_code=404,
                error_message="Entity not found",
                error_code=make_error_code(404, 1),
            )
        del self._store[entity_id]

    def list_all(self) -> List[T]:
        return list(self._store.values())
