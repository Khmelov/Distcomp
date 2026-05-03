from abc import ABC, abstractmethod
from typing import Any, Generic, TypeVar

from app.repositories.paging import PageRequest, PageResult

T = TypeVar("T")


class CrudRepository(ABC, Generic[T]):
    def find_all(self) -> list[T]:
        return self.find_page(PageRequest(page=0, size=10**9, sort=[]), filters=None).items

    @abstractmethod
    def find_page(self, request: PageRequest, filters: dict[str, Any] | None) -> PageResult[T]:
        raise NotImplementedError

    @abstractmethod
    def create(self, entity: T) -> T:
        raise NotImplementedError

    @abstractmethod
    def find_by_id(self, entity_id: int) -> T | None:
        raise NotImplementedError

    @abstractmethod
    def update(self, entity: T) -> T | None:
        raise NotImplementedError

    @abstractmethod
    def delete_by_id(self, entity_id: int) -> bool:
        raise NotImplementedError
