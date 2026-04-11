from __future__ import annotations

from abc import ABC, abstractmethod
from typing import Any, Generic, List, Optional, TypeVar

from publisher.schemas.common import PaginationParams, Page


ModelT = TypeVar("ModelT")


class AbstractRepository(ABC, Generic[ModelT]):
    @abstractmethod
    async def create(self, obj_in: ModelT) -> ModelT:
        raise NotImplementedError

    @abstractmethod
    async def get_by_id(self, obj_id: int) -> Optional[ModelT]:
        raise NotImplementedError

    @abstractmethod
    async def get_all(
        self,
        pagination: PaginationParams,
        filters: Optional[dict[str, Any]] = None,
    ) -> Page[ModelT]:
        raise NotImplementedError

    @abstractmethod
    async def update(self, obj_id: int, obj_in: ModelT) -> Optional[ModelT]:
        raise NotImplementedError

    @abstractmethod
    async def delete(self, obj_id: int) -> bool:
        raise NotImplementedError

