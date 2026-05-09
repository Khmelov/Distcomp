from src.database.uow import UnitOfWork
from src.dto.post import PostRequestTo, PostResponseTo
from src.exceptions import EntityNotFoundException
from src.models.post import Post
from src.repositories.post import AbstractPostRepository
from src.repositories.tweet import AbstractTweetRepository


class PostService:

    def __init__(
        self,
        repository: AbstractPostRepository,
        tweet_repository: AbstractTweetRepository,
        uow: UnitOfWork,
    ) -> None:
        self._repo = repository
        self._tweet_repo = tweet_repository
        self._uow = uow

    async def get_by_id(self, post_id: int) -> PostResponseTo:
        post = await self._repo.get_by_id(post_id)
        if post is None:
            raise EntityNotFoundException("Post", post_id)
        return PostResponseTo.model_validate(post)

    async def get_all(self) -> list[PostResponseTo]:
        posts = await self._repo.get_all()
        return [PostResponseTo.model_validate(p) for p in posts]

    async def create(self, data: PostRequestTo) -> PostResponseTo:
        tweet = await self._tweet_repo.get_by_id(data.tweet_id)
        if tweet is None:
            raise EntityNotFoundException("Tweet", data.tweet_id)
        post = Post(tweet_id=data.tweet_id, content=data.content)
        created = await self._repo.create(post)
        await self._uow.commit()
        return PostResponseTo.model_validate(created)

    async def update(self, post_id: int, data: PostRequestTo) -> PostResponseTo:
        tweet = await self._tweet_repo.get_by_id(data.tweet_id)
        if tweet is None:
            raise EntityNotFoundException("Tweet", data.tweet_id)
        post = Post(tweet_id=data.tweet_id, content=data.content)
        post.id = post_id
        updated = await self._repo.update(post)
        if updated is None:
            raise EntityNotFoundException("Post", post_id)
        await self._uow.commit()
        return PostResponseTo.model_validate(updated)

    async def delete(self, post_id: int) -> None:
        deleted = await self._repo.delete(post_id)
        if not deleted:
            raise EntityNotFoundException("Post", post_id)
        await self._uow.commit()