from abc import abstractmethod, ABC
from typing import Generic, TypeVar, List, Dict

T = TypeVar("T")

class AsyncRepository(ABC, Generic[T]):
    @abstractmethod
    async def get_one(self, entity_id: int) -> T:
        pass
    @abstractmethod
    async def get_all(self) -> List[T]:
        pass
    @abstractmethod
    async def create(self, **kwargs) -> T:
        pass
    @abstractmethod
    async def update(self, entity_id: int, **kwargs) -> T:
        pass
    @abstractmethod
    async def delete(self, entity_id: int) -> None:
        pass