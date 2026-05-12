from typing import List, Optional

from Task320.src.core.constants import ErrorStatus
from Task320.src.core.errors import HttpNotFoundError, HttpBadRequestError
from Task320.src.core.errors.messages import CreatorErrorMessage
from Task320.src.domain.models import Creator, Tweet
from Task320.src.domain.repositories.interfaces import Repository
from Task320.src.schemas.creator import CreatorResponseTo, CreatorRequestTo

class CreatorService:
    def __init__(self, repo: Repository[Creator], tweet_repo: Repository[Tweet]) -> None:
        self._repo = repo
        self._tweet_repo = tweet_repo

    async def get_one(self, creator_id_str: str) -> CreatorResponseTo:
        try:
            creator_id = int(creator_id_str)
        except ValueError:
            raise HttpBadRequestError("Invalid creator ID format", ErrorStatus.BAD_REQUEST)
        try:
            creator = await self._repo.get_one(creator_id)
        except KeyError:
            raise HttpNotFoundError(CreatorErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)
        return CreatorResponseTo.model_validate(creator)

    async def get_all(
        self,
        page: int = 1,
        size: int = 20,
        sort: str = "id",
        login: Optional[str] = None,
        firstname: Optional[str] = None,
        lastname: Optional[str] = None
    ) -> List[CreatorResponseTo]:
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
        return CreatorResponseTo.model_validate(created)

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

    async def get_creator_by_tweet_id(self, tweet_id: int) -> CreatorResponseTo:
        try:
            tweet = await self._tweet_repo.get_one(tweet_id)
        except KeyError:
            raise HttpNotFoundError("Tweet not found", ErrorStatus.NOT_FOUND)
        creator = await self._repo.get_one(tweet.creator_id)
        return CreatorResponseTo.model_validate(creator)