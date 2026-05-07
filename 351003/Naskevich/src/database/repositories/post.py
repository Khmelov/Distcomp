from sqlalchemy import select, delete

from src.database.repositories.base import SQLAlchemyRepository
from src.database.tables import posts_table
from src.models.post import Post
from src.repositories.post import AbstractPostRepository


class PostRepository(SQLAlchemyRepository, AbstractPostRepository):

    async def get_by_id(self, entity_id: int) -> Post | None:
        result = await self._session.execute(
            select(Post).where(posts_table.c.id == entity_id)
        )
        return result.scalar_one_or_none()

    async def get_all(self) -> list[Post]:
        result = await self._session.execute(select(Post))
        return list(result.scalars().all())

    async def create(self, entity: Post) -> Post:
        self._session.add(entity)
        await self._session.flush()
        await self._session.refresh(entity)
        return entity

    async def update(self, entity: Post) -> Post | None:
        existing = await self.get_by_id(entity.id)
        if existing is None:
            return None
        existing.tweet_id = entity.tweet_id
        existing.content = entity.content
        await self._session.flush()
        await self._session.refresh(existing)
        return existing

    async def delete(self, entity_id: int) -> bool:
        result = await self._session.execute(
            delete(Post).where(posts_table.c.id == entity_id)
        )
        return result.rowcount > 0

    async def get_by_tweet_id(self, tweet_id: int) -> list[Post]:
        result = await self._session.execute(
            select(Post).where(posts_table.c.tweet_id == tweet_id)
        )
        return list(result.scalars().all())
