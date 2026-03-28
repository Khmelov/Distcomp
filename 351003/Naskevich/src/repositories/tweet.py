from abc import ABC, abstractmethod

from src.models.tweet import Tweet


class AbstractTweetRepository(ABC):

    @abstractmethod
    async def get_by_id(self, entity_id: int) -> Tweet | None:
        raise NotImplementedError

    @abstractmethod
    async def get_all(self) -> list[Tweet]:
        raise NotImplementedError

    @abstractmethod
    async def create(self, entity: Tweet) -> Tweet:
        raise NotImplementedError

    @abstractmethod
    async def update(self, entity: Tweet) -> Tweet | None:
        raise NotImplementedError

    @abstractmethod
    async def delete(self, entity_id: int) -> bool:
        raise NotImplementedError

    @abstractmethod
    async def get_by_editor_id(self, editor_id: int) -> list[Tweet]:
        raise NotImplementedError

    @abstractmethod
    async def get_by_title(self, title: str) -> Tweet | None:
        raise NotImplementedError