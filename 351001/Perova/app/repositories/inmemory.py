from copy import deepcopy
from typing import Generic, TypeVar

from app.repositories.base import CrudRepository

T = TypeVar("T")


class InMemoryRepository(CrudRepository[T], Generic[T]):
    def __init__(self) -> None:
        self._items: dict[int, T] = {}
        self._next_id = 1

    def create(self, entity: T) -> T:
        entity.id = self._next_id
        self._next_id += 1
        self._items[entity.id] = deepcopy(entity)
        return deepcopy(entity)

    def find_by_id(self, entity_id: int) -> T | None:
        entity = self._items.get(entity_id)
        return deepcopy(entity) if entity is not None else None

    def find_all(self) -> list[T]:
        return [deepcopy(item) for item in self._items.values()]

    def update(self, entity: T) -> T | None:
        if entity.id not in self._items:
            return None
        self._items[entity.id] = deepcopy(entity)
        return deepcopy(entity)

    def delete_by_id(self, entity_id: int) -> bool:
        return self._items.pop(entity_id, None) is not None
