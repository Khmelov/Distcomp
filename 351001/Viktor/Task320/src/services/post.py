from typing import List, Optional

from Task320.src.core.constants import ErrorStatus
from Task320.src.core.errors import HttpNotFoundError, HttpBadRequestError
from Task320.src.core.errors.messages import PostErrorMessage
from Task320.src.domain.models import Post
from Task320.src.domain.repositories.interfaces import Repository
from Task320.src.schemas.post import PostResponseTo, PostRequestTo

class PostService:
    def __init__(self, repo: Repository[Post]) -> None:
        self._repo = repo

    async def get_one(self, post_id_str: str) -> PostResponseTo:
        try:
            post_id = int(post_id_str)
        except ValueError:
            raise HttpBadRequestError("Invalid post ID format", ErrorStatus.BAD_REQUEST)
        try:
            post = await self._repo.get_one(post_id)
        except KeyError:
            raise HttpNotFoundError(PostErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)
        return PostResponseTo.model_validate(post)

    async def get_all(
        self,
        page: int = 1,
        size: int = 20,
        sort: str = "id",
        content: Optional[str] = None,
        tweet_id: Optional[int] = None
    ) -> List[PostResponseTo]:
        posts = await self._repo.get_all(
            page=page, size=size, sort=sort,
            content=content, tweet_id=tweet_id
        )
        return [PostResponseTo.model_validate(p) for p in posts]

    async def create(self, dto: PostRequestTo) -> PostResponseTo:
        post = Post(id=0, content=dto.content, tweet_id=dto.tweet_id)
        created = await self._repo.create(post)
        return PostResponseTo.model_validate(created)

    async def update(self, post_id_str: str, dto: PostRequestTo) -> PostResponseTo:
        try:
            post_id = int(post_id_str)
        except ValueError:
            raise HttpBadRequestError("Invalid post ID format", ErrorStatus.BAD_REQUEST)
        post = Post(id=post_id, content=dto.content, tweet_id=dto.tweet_id)
        try:
            updated = await self._repo.update(post)
        except KeyError:
            raise HttpNotFoundError(PostErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)
        return PostResponseTo.model_validate(updated)

    async def delete(self, post_id_str: str) -> None:
        try:
            post_id = int(post_id_str)
        except ValueError:
            raise HttpBadRequestError("Invalid post ID format", ErrorStatus.BAD_REQUEST)
        try:
            await self._repo.delete(post_id)
        except KeyError:
            raise HttpNotFoundError(PostErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)

    async def get_posts_by_tweet_id(self, tweet_id: int) -> List[PostResponseTo]:
        posts = await self._repo.get_by_tweet_id(tweet_id)
        return [PostResponseTo.model_validate(p) for p in posts]