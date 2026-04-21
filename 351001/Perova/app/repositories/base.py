from abc import ABC, abstractmethod
from typing import Generic, TypeVar

T = TypeVar("T")


class CrudRepository(ABC, Generic[T]):
    @abstractmethod
    def create(self, entity: T) -> T:
        raise NotImplementedError

    @abstractmethod
    def find_by_id(self, entity_id: int) -> T | None:
        raise NotImplementedError

    @abstractmethod
    def find_all(self) -> list[T]:
        raise NotImplementedError

    @abstractmethod
    def update(self, entity: T) -> T | None:
        raise NotImplementedError

    @abstractmethod
    def delete_by_id(self, entity_id: int) -> bool:
        raise NotImplementedError
