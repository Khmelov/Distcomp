from typing import List, Optional

from Task350.publisher.src.core.constants import ErrorStatus
from Task350.publisher.src.core.errors import HttpNotFoundError, HttpBadRequestError, HttpForbiddenError
from Task350.publisher.src.core.errors.messages import TweetErrorMessage
from Task350.publisher.src.domain.models import Tweet, Creator
from Task350.publisher.src.domain.repositories.interfaces import Repository
from Task350.publisher.src.schemas.tweet import TweetResponseTo, TweetRequestTo
from Task350.publisher.src.infrastructure.redis_client import RedisClient

class TweetService:
    def __init__(self, repo: Repository[Tweet], creator_repo: Repository[Creator], redis: RedisClient):
        self._repo = repo
        self._creator_repo = creator_repo
        self._redis = redis

    async def get_one(self, tweet_id_str: str) -> TweetResponseTo:
        try:
            tweet_id = int(tweet_id_str)
        except ValueError:
            raise HttpBadRequestError("Invalid tweet ID format", ErrorStatus.BAD_REQUEST)

        # 1. Пытаемся получить из Redis
        cache_key = f"tweet:{tweet_id}"
        cached = await self._redis.get(cache_key)
        if cached:
            return TweetResponseTo.model_validate(cached)

        # 2. Если нет – из БД
        try:
            tweet = await self._repo.get_one(tweet_id)
        except KeyError:
            raise HttpNotFoundError(TweetErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)

        response = TweetResponseTo.model_validate(tweet)
        await self._redis.set(cache_key, response.model_dump(), ttl=60)
        return response

    # Остальные методы без изменений (get_all, create, update, delete, search)
    async def get_all(self, page: int = 1, size: int = 20, sort: str = "id") -> List[TweetResponseTo]:
        tweets = await self._repo.get_all(page, size, sort)
        return [TweetResponseTo.model_validate(t) for t in tweets]

    async def create(self, dto: TweetRequestTo) -> TweetResponseTo:
        try:
            await self._creator_repo.get_one(dto.creator_id)
        except KeyError:
            raise HttpForbiddenError("Creator not found", ErrorStatus.FORBIDDEN)
        tweet = Tweet(id=0, title=dto.title, content=dto.content, creator_id=dto.creator_id)
        created = await self._repo.create(tweet)
        return TweetResponseTo.model_validate(created)

    async def update(self, tweet_id_str: str, dto: TweetRequestTo) -> TweetResponseTo:
        try:
            tweet_id = int(tweet_id_str)
        except ValueError:
            raise HttpBadRequestError("Invalid tweet ID format", ErrorStatus.BAD_REQUEST)
        tweet = Tweet(id=tweet_id, title=dto.title, content=dto.content, creator_id=dto.creator_id)
        try:
            updated = await self._repo.update(tweet)
        except KeyError:
            raise HttpNotFoundError(TweetErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)
        await self._redis.delete(f"tweet:{tweet_id}")
        return TweetResponseTo.model_validate(updated)

    async def delete(self, tweet_id_str: str) -> None:
        try:
            tweet_id = int(tweet_id_str)
        except ValueError:
            raise HttpBadRequestError("Invalid tweet ID format", ErrorStatus.BAD_REQUEST)
        try:
            await self._repo.delete(tweet_id)
        except KeyError:
            raise HttpNotFoundError(TweetErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)
        await self._redis.delete(f"tweet:{tweet_id}")

    async def search(self, marker_names=None, marker_ids=None, creator_login=None, title=None, content=None, page=1, size=20, sort="id"):
        tweets = await self._repo.search(
            marker_names=marker_names, marker_ids=marker_ids, creator_login=creator_login,
            title=title, content=content, page=page, size=size, sort=sort
        )
        return [TweetResponseTo.model_validate(t) for t in tweets]