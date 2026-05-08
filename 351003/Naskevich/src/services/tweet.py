from datetime import datetime, timezone

from src.cache import keys as cache_keys
from src.cache.redis_cache import RedisCache
from src.database.uow import UnitOfWork
from src.dto.tweet import TweetRequestTo, TweetResponseTo
from src.exceptions import EntityAlreadyExistsException, EntityNotFoundException
from src.models.marker import Marker
from src.models.tweet import Tweet
from src.repositories.editor import AbstractEditorRepository
from src.repositories.marker import AbstractMarkerRepository
from src.repositories.tweet import AbstractTweetRepository


class TweetService:

    def __init__(
        self,
        repository: AbstractTweetRepository,
        editor_repository: AbstractEditorRepository,
        marker_repository: AbstractMarkerRepository,
        uow: UnitOfWork,
        cache: RedisCache,
        cache_ttl_seconds: int,
    ) -> None:
        self._repo = repository
        self._editor_repo = editor_repository
        self._marker_repo = marker_repository
        self._uow = uow
        self._cache = cache
        self._ttl = cache_ttl_seconds

    async def _invalidate_tweets(self, tweet_id: int | None = None) -> None:
        keys = [cache_keys.tweets_all()]
        if tweet_id is not None:
            keys.append(cache_keys.tweet(tweet_id))
        await self._cache.delete(*keys)

    async def _resolve_markers(self, marker_names: list[str]) -> list:
        markers = []
        for name in marker_names:
            marker = await self._marker_repo.get_by_name(name)
            if marker is None:
                marker = Marker(name=name)
                marker = await self._marker_repo.create(marker)
            markers.append(marker)
        return markers

    def _to_response(self, tweet: Tweet) -> TweetResponseTo:
        return TweetResponseTo(
            id=tweet.id,
            editor_id=tweet.editor_id,
            title=tweet.title,
            content=tweet.content,
            created=tweet.created,
            modified=tweet.modified,
            markers=[m.name for m in tweet.markers],
        )

    async def get_by_id(self, tweet_id: int) -> TweetResponseTo:
        ck = cache_keys.tweet(tweet_id)
        cached = await self._cache.get_json(ck)
        if cached is not None:
            return TweetResponseTo.model_validate(cached)
        tweet = await self._repo.get_by_id(tweet_id)
        if tweet is None:
            raise EntityNotFoundException("Tweet", tweet_id)
        dto = self._to_response(tweet)
        await self._cache.set_json(ck, dto.model_dump(mode="json"), ttl_seconds=self._ttl)
        return dto

    async def get_all(self) -> list[TweetResponseTo]:
        ck = cache_keys.tweets_all()
        cached = await self._cache.get_json(ck)
        if cached is not None:
            return [TweetResponseTo.model_validate(x) for x in cached]
        tweets = await self._repo.get_all()
        out = [self._to_response(t) for t in tweets]
        await self._cache.set_json(
            ck,
            [t.model_dump(mode="json") for t in out],
            ttl_seconds=self._ttl,
        )
        return out

    async def create(self, data: TweetRequestTo) -> TweetResponseTo:
        editor = await self._editor_repo.get_by_id(data.editor_id)
        if editor is None:
            raise EntityNotFoundException("Editor", data.editor_id)
        existing = await self._repo.get_by_title(data.title)
        if existing is not None:
            raise EntityAlreadyExistsException("Tweet", "title", data.title)
        now = datetime.now(timezone.utc)
        tweet = Tweet(
            editor_id=data.editor_id,
            title=data.title,
            content=data.content,
            created=now,
            modified=now,
        )
        tweet.markers = await self._resolve_markers(data.markers)
        created = await self._repo.create(tweet)
        await self._uow.commit()
        dto = self._to_response(created)
        await self._invalidate_tweets()
        await self._cache.set_json(
            cache_keys.tweet(dto.id),
            dto.model_dump(mode="json"),
            ttl_seconds=self._ttl,
        )
        return dto

    async def update(self, tweet_id: int, data: TweetRequestTo) -> TweetResponseTo:
        editor = await self._editor_repo.get_by_id(data.editor_id)
        if editor is None:
            raise EntityNotFoundException("Editor", data.editor_id)
        existing = await self._repo.get_by_title(data.title)
        if existing is not None and existing.id != tweet_id:
            raise EntityAlreadyExistsException("Tweet", "title", data.title)
        tweet = Tweet(
            editor_id=data.editor_id,
            title=data.title,
            content=data.content,
            created=datetime.now(timezone.utc),
            modified=datetime.now(timezone.utc),
        )
        tweet.id = tweet_id
        tweet.markers = await self._resolve_markers(data.markers)
        updated = await self._repo.update(tweet)
        if updated is None:
            raise EntityNotFoundException("Tweet", tweet_id)
        await self._uow.commit()
        dto = self._to_response(updated)
        await self._invalidate_tweets(tweet_id)
        await self._cache.set_json(
            cache_keys.tweet(tweet_id),
            dto.model_dump(mode="json"),
            ttl_seconds=self._ttl,
        )
        return dto

    async def delete(self, tweet_id: int) -> None:
        deleted = await self._repo.delete(tweet_id)
        if not deleted:
            raise EntityNotFoundException("Tweet", tweet_id)
        await self._uow.commit()
        await self._invalidate_tweets(tweet_id)
