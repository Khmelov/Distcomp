from typing import Generic, TypeVar, Dict, List, Optional, Callable

T = TypeVar('T')


class InMemoryRepository(Generic[T]):
    def __init__(self):
        self._storage: Dict[int, T] = {}
        self._counter = 1

    def create(self, entity: T) -> T:
        if hasattr(entity, 'id'):
            entity.id = self._counter
            self._counter += 1
        self._storage[entity.id] = entity
        return entity

    def get_all(self) -> List[T]:
        return list(self._storage.values())

    def get_by_id(self, id: int) -> Optional[T]:
        return self._storage.get(id)

    def update(self, id: int, entity: T) -> Optional[T]:
        if id in self._storage:
            entity.id = id  # Убеждаемся, что ID сохраняется
            self._storage[id] = entity
            return entity
        return None

    def delete(self, id: int) -> bool:
        if id in self._storage:
            del self._storage[id]
            return True
        return False


    def find_by_condition(self, condition: Callable[[T], bool]) -> List[T]:
        """Для кастомных фильтров (например, поиск по логину)"""
        return [entity for entity in self._storage.values() if condition(entity)]
