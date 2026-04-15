from __future__ import annotations

from abc import ABC, abstractmethod
from typing import Generic, List, Optional, TypeVar


T = TypeVar("T")


class BaseRepository(ABC, Generic[T]):
    @abstractmethod
    def create(self, entity: T) -> T:
        raise NotImplementedError

    @abstractmethod
    def read_by_id(self, entity_id: int) -> Optional[T]:
        raise NotImplementedError

    @abstractmethod
    def read_all(self) -> List[T]:
        raise NotImplementedError

    @abstractmethod
    def update(self, entity_id: int, entity: T) -> T:
        raise NotImplementedError

    @abstractmethod
    def delete(self, entity_id: int) -> None:
        raise NotImplementedError

