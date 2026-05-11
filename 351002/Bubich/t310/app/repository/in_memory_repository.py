from typing import List, Optional, Dict, TypeVar, Generic
from app.repository.base_repository import BaseRepository

T = TypeVar('T')


class InMemoryRepository(BaseRepository, Generic[T]):
    def __init__(self):
        self._storage: Dict[int, T] = {}
        self._next_id = 1

    def save(self, entity: T) -> T:
        entity.id = self._next_id
        self._storage[self._next_id] = entity
        self._next_id += 1
        return entity

    def find_by_id(self, id: int) -> Optional[T]:
        return self._storage.get(id)

    def find_all(self) -> List[T]:
        return list(self._storage.values())

    def update(self, entity: T) -> T:
        if entity.id not in self._storage:
            raise ValueError(f"Entity with id {entity.id} not found")
        self._storage[entity.id] = entity
        return entity

    def delete_by_id(self, id: int) -> bool:
        if id in self._storage:
            del self._storage[id]
            return True
        return False