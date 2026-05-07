from typing import List

from src.core.constants import ErrorStatus
from src.core.errors import HttpNotFoundError
from src.core.errors.messages import TweetErrorMessage
from src.domain.models import Tweet
from src.domain.repositories.interfaces import Repository
from src.schemas.tweet import TweetResponseTo, TweetRequestTo

class TweetService:
    def __init__(self, repo: Repository[Tweet]) -> None:
        self._repo = repo

    def get_one(self, tweet_id_str: str) -> TweetResponseTo:
        try:
            tweet_id = int(tweet_id_str)
        except ValueError:
            raise HttpNotFoundError(TweetErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)
        try:
            tweet = self._repo.get_one(tweet_id)
        except KeyError:
            raise HttpNotFoundError(TweetErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)
        return TweetResponseTo.model_validate(tweet)

    def get_all(self) -> List[TweetResponseTo]:
        tweets = self._repo.get_all()
        return [TweetResponseTo.model_validate(t) for t in tweets]

    def create(self, dto: TweetRequestTo) -> TweetResponseTo:
        tweet = Tweet(
            id=0,
            title=dto.title,
            content=dto.content,
            creator_id=dto.creator_id
        )
        created = self._repo.create(tweet)
        return TweetResponseTo.model_validate(created)

    def update(self, tweet_id_str: str, dto: TweetRequestTo) -> TweetResponseTo:
        try:
            tweet_id = int(tweet_id_str)
        except ValueError:
            raise HttpNotFoundError(TweetErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)
        tweet = Tweet(
            id=tweet_id,
            title=dto.title,
            content=dto.content,
            creator_id=dto.creator_id
        )
        try:
            updated = self._repo.update(tweet)
        except KeyError:
            raise HttpNotFoundError(TweetErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)
        return TweetResponseTo.model_validate(updated)

    def delete(self, tweet_id_str: str) -> None:
        try:
            tweet_id = int(tweet_id_str)
        except ValueError:
            raise HttpNotFoundError(TweetErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)
        try:
            self._repo.delete(tweet_id)
        except KeyError:
            raise HttpNotFoundError(TweetErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)