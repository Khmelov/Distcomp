import itertools
from typing import Dict, Generic, List, Optional, TypeVar

T = TypeVar("T")

class InMemoryRepository(Generic[T]):
    def __init__(self) -> None:
        self._store: Dict[int, T] = {}
        self._counter = itertools.count(1)

    def next_id(self) -> int:
        return next(self._counter)

    def create(self, entity: T) -> T:
        self._store[entity.id] = entity
        return entity

    def get_by_id(self, entity_id: int) -> Optional[T]:
        return self._store.get(entity_id)

    def get_all(self) -> List[T]:
        return list(self._store.values())

    def update(self, entity_id: int, entity: T) -> Optional[T]:
        if entity_id not in self._store:
            return None
        self._store[entity_id] = entity
        return entity

    def delete(self, entity_id: int) -> bool:
        if entity_id not in self._store:
            return False
        del self._store[entity_id]
        return True
