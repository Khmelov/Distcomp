from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select, text
from typing import List, Optional

from Task320.src.domain.models import Post
from Task320.src.domain.repositories.interfaces import Repository
from Task320.src.infrastructure.models.post_model import PostModel


class SQLAlchemyPostRepository(Repository[Post]):
    def __init__(self, session: AsyncSession):
        self.session = session

    async def get_one(self, entity_id: int) -> Post:
        model = await self.session.get(PostModel, entity_id)
        if not model:
            raise KeyError
        return self._to_domain(model)

    async def get_all(
        self,
        page: int = 1,
        size: int = 20,
        sort: str = "id",
        content: Optional[str] = None,
        tweet_id: Optional[int] = None
    ) -> List[Post]:
        query = select(PostModel)
        if content:
            query = query.where(PostModel.content.ilike(f"%{content}%"))
        if tweet_id is not None:
            query = query.where(PostModel.tweet_id == tweet_id)

        allowed_sort_fields = ['id', 'content', 'tweet_id']
        sort_field = sort.lstrip('-')
        if sort_field not in allowed_sort_fields:
            sort_field = 'id'
        order_column = getattr(PostModel, sort_field)
        if sort.startswith('-'):
            order_column = order_column.desc()
        query = query.order_by(order_column).offset((page - 1) * size).limit(size)

        result = await self.session.execute(query)
        return [self._to_domain(p) for p in result.scalars()]

    async def create(self, entity: Post) -> Post:
        model = PostModel(content=entity.content, tweet_id=entity.tweet_id)
        self.session.add(model)
        await self.session.flush()
        await self.session.refresh(model)
        return self._to_domain(model)

    async def update(self, entity: Post) -> Post:
        model = await self.session.get(PostModel, entity.id)
        if not model:
            raise KeyError
        model.content = entity.content
        model.tweet_id = entity.tweet_id
        await self.session.flush()
        return self._to_domain(model)

    async def delete(self, entity_id: int) -> None:
        model = await self.session.get(PostModel, entity_id)
        if not model:
            raise KeyError
        await self.session.delete(model)
        await self.session.flush()

    async def get_by_tweet_id(self, tweet_id: int) -> List[Post]:
        query = select(PostModel).where(PostModel.tweet_id == tweet_id)
        result = await self.session.execute(query)
        models = result.scalars().all()
        return [self._to_domain(m) for m in models]

    def _to_domain(self, model: PostModel) -> Post:
        return Post(id=model.id, content=model.content, tweet_id=model.tweet_id)