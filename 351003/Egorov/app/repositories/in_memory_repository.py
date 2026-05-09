from __future__ import annotations

from typing import Dict, Generic, List, Optional, TypeVar

from .base_repository import BaseRepository


T = TypeVar("T")


class InMemoryRepository(BaseRepository[T], Generic[T]):
    def __init__(self) -> None:
        self._storage: Dict[int, T] = {}
        self._next_id: int = 1

    def create(self, entity: T) -> T:
        entity_id = self._next_id
        self._next_id += 1
        setattr(entity, "id", entity_id)
        self._storage[entity_id] = entity
        return entity

    def read_by_id(self, entity_id: int) -> Optional[T]:
        return self._storage.get(entity_id)

    def read_all(self) -> List[T]:
        return list(self._storage.values())

    def update(self, entity_id: int, entity: T) -> T:
        if entity_id not in self._storage:
            raise KeyError(entity_id)
        setattr(entity, "id", entity_id)
        self._storage[entity_id] = entity
        return entity

    def delete(self, entity_id: int) -> None:
        if entity_id not in self._storage:
            raise KeyError(entity_id)
        del self._storage[entity_id]

