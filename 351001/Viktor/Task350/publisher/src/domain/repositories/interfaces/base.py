from abc import abstractmethod, ABC
from typing import Generic, TypeVar, List, Dict

T = TypeVar("T")

class Repository(ABC, Generic[T]):
    @abstractmethod
    async def get_one(self, entity_id: int) -> T:
        pass

    @abstractmethod
    async def get_all(self, page: int = 1, size: int = 20, sort: str = "id") -> List[T]:
        pass

    @abstractmethod
    async def create(self, entity: T) -> T:
        pass

    @abstractmethod
    async def update(self, entity: T) -> T:
        pass

    @abstractmethod
    async def delete(self, entity_id: int) -> None:
        pass


class InMemoryRepository(Repository[T]):
    def __init__(self) -> None:
        self._data: Dict[int, T] = {}
        self._id = 0

    def _next_id(self) -> int:
        self._id += 1
        return self._id

    async def get_one(self, entity_id: int) -> T:
        return self._data[entity_id]

    async def get_all(self, page: int = 1, size: int = 20, sort: str = "id") -> List[T]:
        # InMemory игнорирует пагинацию/сортировку, возвращаем всё
        return list(self._data.values())

    async def create(self, entity: T) -> T:
        new_id = self._next_id()
        entity.id = new_id
        self._data[new_id] = entity
        return entity

    async def update(self, entity: T) -> T:
        self._data[entity.id] = entity
        return entity

    async def delete(self, entity_id: int) -> None:
        self._data.pop(entity_id)