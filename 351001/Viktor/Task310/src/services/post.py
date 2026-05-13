from typing import List

from Task310.src.core.constants import ErrorStatus
from Task310.src.core.errors import HttpNotFoundError
from Task310.src.core.errors.messages import PostErrorMessage
from Task310.src.domain.models import Post
from Task310.src.domain.repositories.interfaces import Repository
from Task310.src.schemas.post import PostResponseTo, PostRequestTo

class PostService:
    def __init__(self, repo: Repository[Post]) -> None:
        self._repo = repo

    def get_one(self, post_id_str: str) -> PostResponseTo:
        try:
            post_id = int(post_id_str)
        except ValueError:
            raise HttpNotFoundError(PostErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)
        try:
            post = self._repo.get_one(post_id)
        except KeyError:
            raise HttpNotFoundError(PostErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)
        return PostResponseTo.model_validate(post)

    def get_all(self) -> List[PostResponseTo]:
        posts = self._repo.get_all()
        return [PostResponseTo.model_validate(p) for p in posts]

    def create(self, dto: PostRequestTo) -> PostResponseTo:
        post = Post(
            id=0,
            content=dto.content,
            tweet_id=dto.tweet_id
        )
        created = self._repo.create(post)
        return PostResponseTo.model_validate(created)

    def update(self, post_id_str: str, dto: PostRequestTo) -> PostResponseTo:
        try:
            post_id = int(post_id_str)
        except ValueError:
            raise HttpNotFoundError(PostErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)
        post = Post(
            id=post_id,
            content=dto.content,
            tweet_id=dto.tweet_id
        )
        try:
            updated = self._repo.update(post)
        except KeyError:
            raise HttpNotFoundError(PostErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)
        return PostResponseTo.model_validate(updated)

    def delete(self, post_id_str: str) -> None:
        try:
            post_id = int(post_id_str)
        except ValueError:
            raise HttpNotFoundError(PostErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)
        try:
            self._repo.delete(post_id)
        except KeyError:
            raise HttpNotFoundError(PostErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)