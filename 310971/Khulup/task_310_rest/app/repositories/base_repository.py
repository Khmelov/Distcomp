from abc import ABC, abstractmethod
from typing import Generic, TypeVar, Optional, List

T = TypeVar('T')

class BaseRepository(ABC, Generic[T]):
    @abstractmethod
    def add(self, entity_data) -> T:
        pass

    @abstractmethod
    def get_by_id(self, entity_id: int) -> Optional[T]:
        pass

    @abstractmethod
    def list(self) -> List[T]:
        pass

    @abstractmethod
    def delete(self, entity_id: int) -> bool:
        pass

    @abstractmethod
    def update(self, entity_data) -> Optional[T]:
        pass
