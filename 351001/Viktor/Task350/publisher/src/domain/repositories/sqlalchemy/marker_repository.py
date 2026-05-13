from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from typing import List, Optional

from Task350.publisher.src.domain.models import Marker
from Task350.publisher.src.domain.repositories.interfaces import Repository
from Task350.publisher.src.infrastructure.models.marker_model import MarkerModel
from Task350.publisher.src.infrastructure.models.associations import tweet_marker_association


class SQLAlchemyMarkerRepository(Repository[Marker]):
    def __init__(self, session: AsyncSession):
        self.session = session

    async def get_one(self, entity_id: int) -> Marker:
        model = await self.session.get(MarkerModel, entity_id)
        if not model:
            raise KeyError
        return self._to_domain(model)

    async def get_all(
        self,
        page: int = 1,
        size: int = 20,
        sort: str = "id",
        name: Optional[str] = None
    ) -> List[Marker]:
        query = select(MarkerModel)
        if name:
            query = query.where(MarkerModel.name.ilike(f"%{name}%"))

        allowed_sort_fields = ['id', 'name']
        sort_field = sort.lstrip('-')
        if sort_field not in allowed_sort_fields:
            sort_field = 'id'
        order_column = getattr(MarkerModel, sort_field)
        if sort.startswith('-'):
            order_column = order_column.desc()
        query = query.order_by(order_column).offset((page - 1) * size).limit(size)

        result = await self.session.execute(query)
        return [self._to_domain(m) for m in result.scalars()]

    async def create(self, entity: Marker) -> Marker:
        model = MarkerModel(name=entity.name)
        self.session.add(model)
        await self.session.flush()
        await self.session.refresh(model)
        return self._to_domain(model)

    async def update(self, entity: Marker) -> Marker:
        model = await self.session.get(MarkerModel, entity.id)
        if not model:
            raise KeyError
        model.name = entity.name
        await self.session.flush()
        return self._to_domain(model)

    async def delete(self, entity_id: int) -> None:
        model = await self.session.get(MarkerModel, entity_id)
        if not model:
            raise KeyError
        await self.session.delete(model)
        await self.session.flush()

    async def get_by_tweet_id(self, tweet_id: int) -> List[Marker]:
        query = select(MarkerModel).distinct().join(
            tweet_marker_association, MarkerModel.id == tweet_marker_association.c.marker_id
        ).where(tweet_marker_association.c.tweet_id == tweet_id)
        result = await self.session.execute(query)
        models = result.scalars().all()
        return [self._to_domain(m) for m in models]

    def _to_domain(self, model: MarkerModel) -> Marker:
        return Marker(id=model.id, name=model.name)