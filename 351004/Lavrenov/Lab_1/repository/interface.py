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
