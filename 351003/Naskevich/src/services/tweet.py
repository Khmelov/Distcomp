from datetime import datetime, timezone

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
    ) -> None:
        self._repo = repository
        self._editor_repo = editor_repository
        self._marker_repo = marker_repository
        self._uow = uow

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
        tweet = await self._repo.get_by_id(tweet_id)
        if tweet is None:
            raise EntityNotFoundException("Tweet", tweet_id)
        return self._to_response(tweet)

    async def get_all(self) -> list[TweetResponseTo]:
        tweets = await self._repo.get_all()
        return [self._to_response(t) for t in tweets]

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
        return self._to_response(created)

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
        return self._to_response(updated)

    async def delete(self, tweet_id: int) -> None:
        deleted = await self._repo.delete(tweet_id)
        if not deleted:
            raise EntityNotFoundException("Tweet", tweet_id)
        await self._uow.commit()