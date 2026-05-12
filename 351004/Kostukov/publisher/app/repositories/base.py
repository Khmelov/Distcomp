from typing import Generic, TypeVar, Optional, List, Dict, Any, Tuple
from abc import ABC, abstractmethod

T = TypeVar("T")
Filter = Dict[str, Any]
Sort = List[Tuple[str, str]]

class BaseRepository(ABC, Generic[T]):
    @abstractmethod
    async def create(self, obj: T) -> T:
        pass

    @abstractmethod
    async def get_by_id(self, id: int) -> Optional[T]:
        pass

    @abstractmethod
    async def update(self, id: int, obj: T) -> T:
        pass

    @abstractmethod
    async def delete(self, id: int) -> None:
        pass

    @abstractmethod
    async def list(
        self, filters: Filter = None, page: int = 1, size: int = 20, sort: Sort = None) -> List[T]:
        pass