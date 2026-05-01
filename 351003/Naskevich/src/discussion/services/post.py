from src.discussion.moderation import moderate_content
from src.discussion.publisher_client import PublisherClient
from src.dto.post import PostRequestTo, PostResponseTo
from src.exceptions import EntityNotFoundException
from src.models.post import Post
from src.repositories.post import AbstractPostRepository


class PostService:
    def __init__(
        self,
        repository: AbstractPostRepository,
        publisher: PublisherClient,
    ) -> None:
        self._repo = repository
        self._publisher = publisher

    async def get_by_tweet_id(self, tweet_id: int) -> list[PostResponseTo]:
        await self._publisher.ensure_tweet_exists(tweet_id)
        posts = await self._repo.get_by_tweet_id(tweet_id)
        return [PostResponseTo.model_validate(p) for p in posts]

    async def get_all(self) -> list[PostResponseTo]:
        posts = await self._repo.get_all()
        return [PostResponseTo.model_validate(p) for p in posts]

    async def get_by_id(self, post_id: int) -> PostResponseTo:
        post = await self._repo.get_by_id(post_id)
        if post is None:
            raise EntityNotFoundException("Post", post_id)
        return PostResponseTo.model_validate(post)

    async def create(self, data: PostRequestTo) -> PostResponseTo:
        await self._publisher.ensure_tweet_exists(data.tweet_id)
        post = Post(
            tweet_id=data.tweet_id,
            content=data.content,
            state=moderate_content(data.content),
        )
        created = await self._repo.create(post)
        return PostResponseTo.model_validate(created)

    async def update(self, post_id: int, data: PostRequestTo) -> PostResponseTo:
        await self._publisher.ensure_tweet_exists(data.tweet_id)
        post = Post(
            tweet_id=data.tweet_id,
            content=data.content,
            state=moderate_content(data.content),
        )
        post.id = post_id
        updated = await self._repo.update(post)
        if updated is None:
            raise EntityNotFoundException("Post", post_id)
        return PostResponseTo.model_validate(updated)

    async def delete(self, post_id: int) -> None:
        deleted = await self._repo.delete(post_id)
        if not deleted:
            raise EntityNotFoundException("Post", post_id)
