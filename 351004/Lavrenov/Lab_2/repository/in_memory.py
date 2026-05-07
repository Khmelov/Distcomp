from typing import Dict, List, Optional, TypeVar, Generic
from copy import deepcopy
from datetime import datetime
from .interface import IRepository

T = TypeVar("T")


class InMemoryRepository(IRepository[T], Generic[T]):
    def __init__(self):
        self._storage: Dict[int, T] = {}
        self._counter = 0

    def _next_id(self) -> int:
        self._counter += 1
        return self._counter

    def get(self, id: int) -> Optional[T]:
        entity = self._storage.get(id)
        return deepcopy(entity) if entity else None

    def get_all(self) -> List[T]:
        return [deepcopy(e) for e in self._storage.values()]

    def create(self, entity: T) -> T:
        if hasattr(entity, "id") and entity.id is None:
            entity.id = self._next_id()
        now = datetime.utcnow()
        if hasattr(entity, "created") and entity.created is None:
            entity.created = now
        if hasattr(entity, "modified") and entity.modified is None:
            entity.modified = now
        self._storage[entity.id] = deepcopy(entity)
        return deepcopy(entity)

    def update(self, entity: T) -> T:
        if entity.id not in self._storage:
            raise ValueError(f"Entity with id {entity.id} not found")
        if hasattr(entity, "modified"):
            entity.modified = datetime.utcnow()
        self._storage[entity.id] = deepcopy(entity)
        return deepcopy(entity)

    def delete(self, id: int) -> bool:
        if id in self._storage:
            del self._storage[id]
            return True
        return False
