from typing import Generic, TypeVar

from .base_repository import BaseRepository

T = TypeVar("T")


class InMemoryRepository(BaseRepository[T], Generic[T]):
    def __init__(self):
        self._storage: dict[int, T] = {}
        self._counter: int = 1

    def _next_id(self) -> int:
        current = self._counter
        self._counter += 1
        return current

    def create(self, entity: T) -> T:
        entity.id = self._next_id()
        self._storage[entity.id] = entity
        return entity

    def get_by_id(self, entity_id: int) -> T | None:
        return self._storage.get(entity_id)

    def get_all(self) -> list[T]:
        return list(self._storage.values())

    def update(self, entity: T) -> T:
        self._storage[entity.id] = entity
        return entity

    def delete(self, entity_id: int) -> bool:
        if entity_id not in self._storage:
            return False
        del self._storage[entity_id]
        return True