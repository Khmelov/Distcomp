from abc import ABC, abstractmethod
from typing import List, Optional, TypeVar, Generic

T = TypeVar("T")


class IRepository(ABC, Generic[T]):
    @abstractmethod
    def get(self, id: int) -> Optional[T]:
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
    def delete(self, id: int) -> bool:
        pass

    @abstractmethod
    def find_all(
        self, filters: dict, offset: int, limit: int, sort_by: str, order: str
    ) -> List[T]:
        pass

    @abstractmethod
    def count(self, filters: dict) -> int:
        pass
