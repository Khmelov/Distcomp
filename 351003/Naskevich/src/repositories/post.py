from abc import ABC, abstractmethod

from src.models.post import Post


class AbstractPostRepository(ABC):

    @abstractmethod
    async def get_by_id(self, entity_id: int) -> Post | None:
        raise NotImplementedError

    @abstractmethod
    async def create(self, entity: Post) -> Post:
        raise NotImplementedError

    @abstractmethod
    async def update(self, entity: Post) -> Post | None:
        raise NotImplementedError

    @abstractmethod
    async def delete(self, entity_id: int) -> bool:
        raise NotImplementedError

    @abstractmethod
    async def get_by_tweet_id(self, tweet_id: int) -> list[Post]:
        raise NotImplementedError

    @abstractmethod
    async def get_all(self) -> list[Post]:
        raise NotImplementedError
