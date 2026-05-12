from typing import List, Optional

from Task320.src.core.constants import ErrorStatus
from Task320.src.core.errors import HttpNotFoundError, HttpBadRequestError, HttpForbiddenError
from Task320.src.core.errors.messages import TweetErrorMessage
from Task320.src.domain.models import Tweet, Creator
from Task320.src.domain.repositories.interfaces import Repository
from Task320.src.schemas.tweet import TweetResponseTo, TweetRequestTo

class TweetService:
    def __init__(self, repo: Repository[Tweet], creator_repo: Repository[Creator]) -> None:
        self._repo = repo
        self._creator_repo = creator_repo

    async def get_one(self, tweet_id_str: str) -> TweetResponseTo:
        try:
            tweet_id = int(tweet_id_str)
        except ValueError:
            raise HttpBadRequestError("Invalid tweet ID format", ErrorStatus.BAD_REQUEST)
        try:
            tweet = await self._repo.get_one(tweet_id)
        except KeyError:
            raise HttpNotFoundError(TweetErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)
        return TweetResponseTo.model_validate(tweet)

    async def get_all(self, page: int = 1, size: int = 20, sort: str = "id") -> List[TweetResponseTo]:
        tweets = await self._repo.get_all(page, size, sort)
        return [TweetResponseTo.model_validate(t) for t in tweets]

    async def create(self, dto: TweetRequestTo) -> TweetResponseTo:
        # Проверяем существование создателя
        try:
            await self._creator_repo.get_one(dto.creator_id)
        except KeyError:
            raise HttpForbiddenError("Creator not found", ErrorStatus.FORBIDDEN)

        tweet = Tweet(
            id=0,
            title=dto.title,
            content=dto.content,
            creator_id=dto.creator_id
        )
        created = await self._repo.create(tweet)
        return TweetResponseTo.model_validate(created)

    async def update(self, tweet_id_str: str, dto: TweetRequestTo) -> TweetResponseTo:
        try:
            tweet_id = int(tweet_id_str)
        except ValueError:
            raise HttpBadRequestError("Invalid tweet ID format", ErrorStatus.BAD_REQUEST)
        tweet = Tweet(
            id=tweet_id,
            title=dto.title,
            content=dto.content,
            creator_id=dto.creator_id
        )
        try:
            updated = await self._repo.update(tweet)
        except KeyError:
            raise HttpNotFoundError(TweetErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)
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

    async def search(
        self,
        marker_names: Optional[List[str]] = None,
        marker_ids: Optional[List[int]] = None,
        creator_login: Optional[str] = None,
        title: Optional[str] = None,
        content: Optional[str] = None,
        page: int = 1,
        size: int = 20,
        sort: str = "id"
    ) -> List[TweetResponseTo]:
        tweets = await self._repo.search(
            marker_names=marker_names,
            marker_ids=marker_ids,
            creator_login=creator_login,
            title=title,
            content=content,
            page=page,
            size=size,
            sort=sort
        )
        return [TweetResponseTo.model_validate(t) for t in tweets]