from sqlalchemy import select, delete

from src.database.repositories.base import SQLAlchemyRepository
from src.database.tables import tweets_table
from src.models.tweet import Tweet
from src.repositories.tweet import AbstractTweetRepository


class TweetRepository(SQLAlchemyRepository, AbstractTweetRepository):

    async def get_by_id(self, entity_id: int) -> Tweet | None:
        result = await self._session.execute(
            select(Tweet).where(tweets_table.c.id == entity_id)
        )
        return result.scalar_one_or_none()

    async def get_all(self) -> list[Tweet]:
        result = await self._session.execute(select(Tweet))
        return list(result.scalars().all())

    async def create(self, entity: Tweet) -> Tweet:
        self._session.add(entity)
        await self._session.flush()
        await self._session.refresh(entity)
        return entity

    async def update(self, entity: Tweet) -> Tweet | None:
        existing = await self.get_by_id(entity.id)
        if existing is None:
            return None
        existing.editor_id = entity.editor_id
        existing.title = entity.title
        existing.content = entity.content
        existing.markers = entity.markers
        await self._session.flush()
        await self._session.refresh(existing)
        return existing

    async def delete(self, entity_id: int) -> bool:
        result = await self._session.execute(
            delete(Tweet).where(tweets_table.c.id == entity_id)
        )
        return result.rowcount > 0  # type: ignore

    async def get_by_editor_id(self, editor_id: int) -> list[Tweet]:
        result = await self._session.execute(
            select(Tweet).where(tweets_table.c.editor_id == editor_id)
        )
        return list(result.scalars().all())

    async def get_by_title(self, title: str) -> Tweet | None:
        result = await self._session.execute(
            select(Tweet).where(tweets_table.c.title == title)
        )
        return result.scalar_one_or_none()

