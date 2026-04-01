from abc import ABC, abstractmethod

from src.models.marker import Marker


class AbstractMarkerRepository(ABC):

    @abstractmethod
    async def get_by_id(self, entity_id: int) -> Marker | None:
        raise NotImplementedError

    @abstractmethod
    async def get_all(self) -> list[Marker]:
        raise NotImplementedError

    @abstractmethod
    async def create(self, entity: Marker) -> Marker:
        raise NotImplementedError

    @abstractmethod
    async def update(self, entity: Marker) -> Marker | None:
        raise NotImplementedError

    @abstractmethod
    async def delete(self, entity_id: int) -> bool:
        raise NotImplementedError

    @abstractmethod
    async def get_by_name(self, name: str) -> Marker | None:
        raise NotImplementedError 
