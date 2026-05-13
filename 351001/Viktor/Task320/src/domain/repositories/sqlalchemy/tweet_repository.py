from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select, text, and_, or_
from typing import List, Optional
from datetime import datetime

from Task320.src.domain.models import Tweet
from Task320.src.domain.repositories.interfaces import Repository
from Task320.src.infrastructure.models.tweet_model import TweetModel
from Task320.src.infrastructure.models.creator_model import CreatorModel
from Task320.src.infrastructure.models.marker_model import MarkerModel
from Task320.src.infrastructure.models.associations import tweet_marker_association


class SQLAlchemyTweetRepository(Repository[Tweet]):
    def __init__(self, session: AsyncSession):
        self.session = session

    async def get_one(self, entity_id: int) -> Tweet:
        model = await self.session.get(TweetModel, entity_id)
        if not model:
            raise KeyError
        return self._to_domain(model)

    async def get_all(self, page: int = 1, size: int = 20, sort: str = "id") -> List[Tweet]:
        order_by = text(sort) if sort else text("id")
        query = select(TweetModel).order_by(order_by).offset((page - 1) * size).limit(size)
        result = await self.session.execute(query)
        models = result.scalars().all()
        return [self._to_domain(m) for m in models]

    async def create(self, entity: Tweet) -> Tweet:
        model = TweetModel(
            title=entity.title,
            content=entity.content,
            creator_id=entity.creator_id,
            created_at=datetime.now(),
            updated_at=datetime.now()
        )
        self.session.add(model)
        await self.session.flush()
        await self.session.refresh(model)
        return self._to_domain(model)

    async def update(self, entity: Tweet) -> Tweet:
        model = await self.session.get(TweetModel, entity.id)
        if not model:
            raise KeyError
        model.title = entity.title
        model.content = entity.content
        model.creator_id = entity.creator_id
        model.updated_at = datetime.now()
        await self.session.flush()
        return self._to_domain(model)

    async def delete(self, entity_id: int) -> None:
        model = await self.session.get(TweetModel, entity_id)
        if not model:
            raise KeyError
        await self.session.delete(model)
        await self.session.flush()

    # Дополнительные методы для поиска
    async def search(
        self,
        marker_names: Optional[List[str]] = None,
        marker_ids: Optional[List[int]] = None,
        creator_login: Optional[str] = None,
        title: Optional[str] = None,
        content: Optional[str] = None,
        page: int = 1,
        size: int = 20,
        sort: str = "id"
    ) -> List[Tweet]:
        query = select(TweetModel).distinct()

        if marker_names or marker_ids:
            query = query.join(tweet_marker_association).join(MarkerModel)

        conditions = []
        if marker_names:
            conditions.append(MarkerModel.name.in_(marker_names))
        if marker_ids:
            conditions.append(MarkerModel.id.in_(marker_ids))
        if creator_login:
            query = query.join(CreatorModel)
            conditions.append(CreatorModel.login == creator_login)
        if title:
            conditions.append(TweetModel.title.ilike(f"%{title}%"))
        if content:
            conditions.append(TweetModel.content.ilike(f"%{content}%"))

        if conditions:
            query = query.where(and_(*conditions))

        order_by = text(sort) if sort else text("id")
        query = query.order_by(order_by).offset((page - 1) * size).limit(size)

        result = await self.session.execute(query)
        models = result.scalars().all()
        return [self._to_domain(m) for m in models]

    async def get_by_creator_login(self, creator_login: str) -> List[Tweet]:
        query = select(TweetModel).join(CreatorModel).where(CreatorModel.login == creator_login)
        result = await self.session.execute(query)
        models = result.scalars().all()
        return [self._to_domain(m) for m in models]

    async def get_by_marker_ids(self, marker_ids: List[int]) -> List[Tweet]:
        query = select(TweetModel).distinct().join(tweet_marker_association).where(
            tweet_marker_association.c.marker_id.in_(marker_ids)
        )
        result = await self.session.execute(query)
        models = result.scalars().all()
        return [self._to_domain(m) for m in models]

    async def add_markers(self, tweet_id: int, marker_ids: List[int]) -> None:
        tweet = await self.session.get(TweetModel, tweet_id)
        if not tweet:
            raise KeyError(f"Tweet with id {tweet_id} not found")
        markers = await self.session.execute(
            select(MarkerModel).where(MarkerModel.id.in_(marker_ids))
        )
        existing_markers = markers.scalars().all()
        # Добавляем только те, которых ещё нет в твите (чтобы избежать дубликатов)
        current_marker_ids = {m.id for m in tweet.markers}
        for marker in existing_markers:
            if marker.id not in current_marker_ids:
                tweet.markers.append(marker)
        await self.session.flush()

    def _to_domain(self, model: TweetModel) -> Tweet:
        return Tweet(
            id=model.id,
            title=model.title,
            content=model.content,
            creator_id=model.creator_id,
            created_at=model.created_at,
            updated_at=model.updated_at
        )