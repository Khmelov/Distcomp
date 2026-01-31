from abc import abstractmethod, ABC
from typing import Generic, TypeVar, List, Dict

T = TypeVar("T")

class Repository(ABC, Generic[T]):
    @abstractmethod
    def get_one(self, entity_id: int) -> T:
        pass
    @abstractmethod
    def get_all(self) -> List[T]:
        pass
    @abstractmethod
    def create(self, entity: T) -> T:
        pass
    @abstractmethod
    def update(self, entity: T) -> T:
        pass
    @abstractmethod
    def delete(self, entity_id: int) -> None:
        pass

class InMemoryRepository(Repository[T]):
    def __init__(self) -> None:
        self._data: Dict[int, T] = {}
        self._id = 0

    def _next_id(self) -> int:
        self._id += 1
        return self._id

    def get_one(self, entity_id: int) -> T:
        return self._data[entity_id]

    def get_all(self) -> List[T]:
        return list(self._data.values())

    def create(self, entity: T) -> T:
        new_id = self._next_id()
        entity.id = new_id
        self._data[new_id] = entity
        return entity

    def update(self, entity: T) -> T:
        self._data[entity.id] = entity
        return entity

    def delete(self, entity_id: int) -> None:
        self._data.pop(entity_id)