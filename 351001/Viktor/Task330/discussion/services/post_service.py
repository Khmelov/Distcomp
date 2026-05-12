from ...publisher.src.core.errors import HttpNotFoundError
from Task330.discussion.domain.repositories.post_repository import CassandraPostRepository

class PostService:
    def __init__(self, repo: CassandraPostRepository):
        self._repo = repo

    async def get_one(self, tweet_id: int, post_id: int):
        post = await self._repo.get_one(tweet_id, post_id)
        if not post:
            raise HttpNotFoundError("Post not found", 40401)
        return post

    async def get_all(self, tweet_id: int, page: int, size: int):
        return await self._repo.get_all(tweet_id, page, size)

    async def create(self, tweet_id: int, content: str):
        return await self._repo.create(tweet_id, content)

    async def update(self, tweet_id: int, post_id: int, content: str):
        return await self._repo.update(tweet_id, post_id, content)

    async def delete(self, tweet_id: int, post_id: int):
        await self._repo.delete(tweet_id, post_id)