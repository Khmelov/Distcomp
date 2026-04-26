from typing import List, Optional

from Task350.publisher.src.core.constants import ErrorStatus
from Task350.publisher.src.core.errors import HttpNotFoundError, HttpBadRequestError
from Task350.publisher.src.core.errors.messages import CreatorErrorMessage
from Task350.publisher.src.domain.models import Creator, Tweet
from Task350.publisher.src.domain.repositories.interfaces import Repository
from Task350.publisher.src.schemas.creator import CreatorResponseTo, CreatorRequestTo
from Task350.publisher.src.infrastructure.redis_client import RedisClient
import json

class CreatorService:
    def __init__(self, repo: Repository[Creator], tweet_repo: Repository[Tweet], redis: RedisClient):
        self._repo = repo
        self._tweet_repo = tweet_repo
        self._redis = redis

    async def get_one(self, creator_id_str: str) -> CreatorResponseTo:
        try:
            creator_id = int(creator_id_str)
        except ValueError:
            raise HttpBadRequestError("Invalid creator ID format", ErrorStatus.BAD_REQUEST)

        # 1. Пробуем получить из Redis
        cache_key = f"creator:{creator_id}"
        cached = await self._redis.get(cache_key)
        if cached:
            return CreatorResponseTo.model_validate(cached)

        # 2. Если нет – из БД
        try:
            creator = await self._repo.get_one(creator_id)
        except KeyError:
            raise HttpNotFoundError(CreatorErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)

        # 3. Сохраняем в Redis (TTL 60 сек)
        response = CreatorResponseTo.model_validate(creator)
        await self._redis.set(cache_key, response.model_dump(), ttl=60)
        return response

    async def get_all(
        self,
        page: int = 1,
        size: int = 20,
        sort: str = "id",
        login: Optional[str] = None,
        firstname: Optional[str] = None,
        lastname: Optional[str] = None
    ) -> List[CreatorResponseTo]:
        # Для списков кеширование сложнее – можно кешировать по параметрам,
        # но для простоты не кешируем. Или кешируем с большим TTL.
        creators = await self._repo.get_all(
            page=page, size=size, sort=sort,
            login=login, firstname=firstname, lastname=lastname
        )
        return [CreatorResponseTo.model_validate(c) for c in creators]

    async def create(self, dto: CreatorRequestTo) -> CreatorResponseTo:
        creator = Creator(
            id=0,
            login=dto.login,
            password=dto.password,
            firstname=dto.firstname,
            lastname=dto.lastname
        )
        created = await self._repo.create(creator)
        response = CreatorResponseTo.model_validate(created)
        # При создании кеш не трогаем (т.к. новый id, его ещё нет)
        return response

    async def update(self, creator_id_str: str, dto: CreatorRequestTo) -> CreatorResponseTo:
        try:
            creator_id = int(creator_id_str)
        except ValueError:
            raise HttpBadRequestError("Invalid creator ID format", ErrorStatus.BAD_REQUEST)
        creator = Creator(
            id=creator_id,
            login=dto.login,
            password=dto.password,
            firstname=dto.firstname,
            lastname=dto.lastname
        )
        try:
            updated = await self._repo.update(creator)
        except KeyError:
            raise HttpNotFoundError(CreatorErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)

        # Инвалидация кеша
        await self._redis.delete(f"creator:{creator_id}")
        return CreatorResponseTo.model_validate(updated)

    async def delete(self, creator_id_str: str) -> None:
        try:
            creator_id = int(creator_id_str)
        except ValueError:
            raise HttpBadRequestError("Invalid creator ID format", ErrorStatus.BAD_REQUEST)
        try:
            await self._repo.delete(creator_id)
        except KeyError:
            raise HttpNotFoundError(CreatorErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)
        # Инвалидация кеша
        await self._redis.delete(f"creator:{creator_id}")

    async def get_creator_by_tweet_id(self, tweet_id: int) -> CreatorResponseTo:
        # Можно также кешировать, но для простоты пропустим
        try:
            tweet = await self._tweet_repo.get_one(tweet_id)
        except KeyError:
            raise HttpNotFoundError("Tweet not found", ErrorStatus.NOT_FOUND)
        creator = await self._repo.get_one(tweet.creator_id)
        return CreatorResponseTo.model_validate(creator)