# app/repositories/marker_repo.py
from typing import List, Dict, Any, Optional, Tuple
from sqlalchemy import select, asc, desc
from sqlalchemy.ext.asyncio import AsyncSession
from app.infrastructure.db.models import Marker as MarkerModel
from app.repositories.base import BaseRepository

class MarkerRepository(BaseRepository[MarkerModel]):
    def __init__(self, session: AsyncSession):
        self.session = session

    async def create(self, obj: MarkerModel) -> MarkerModel:
        self.session.add(obj)
        await self.session.flush()
        await self.session.commit()
        await self.session.refresh(obj)
        return obj

    async def get_by_id(self, id: int) -> Optional[MarkerModel]:
        q = select(MarkerModel).where(MarkerModel.id == id)
        res = await self.session.execute(q)
        return res.scalar_one_or_none()

    async def get_by_name(self, name: str) -> Optional[MarkerModel]:
        q = select(MarkerModel).where(MarkerModel.name == name)
        res = await self.session.execute(q)
        return res.scalar_one_or_none()

    async def update(self, id: int, obj: MarkerModel) -> MarkerModel:
        existing = await self.get_by_id(id)
        if not existing:
            raise KeyError("not found")
        existing.name = obj.name
        await self.session.commit()
        await self.session.refresh(existing)
        return existing

    async def delete(self, id: int) -> None:
        existing = await self.get_by_id(id)
        if not existing:
            raise KeyError("not found")
        await self.session.delete(existing)
        await self.session.commit()

    async def list(self, filters: Dict[str, Any] | None = None, page: int = 1, size: int = 20, sort: List[Tuple[str,str]] | None = None) -> List[MarkerModel]:
        q = select(MarkerModel)
        if filters:
            if "name" in filters:
                q = q.where(MarkerModel.name.ilike(f"%{filters['name']}%"))
        if sort:
            for field, direction in sort:
                col = getattr(MarkerModel, field, None)
                if col is not None:
                    q = q.order_by(desc(col) if direction.lower()=="desc" else asc(col))
        q = q.offset((page-1)*size).limit(size)
        res = await self.session.execute(q)
        return res.scalars().all()

    async def find_ids_by_names(self, names: List[str]) -> List[int]:
        q = select(MarkerModel.id).where(MarkerModel.name.in_(names))
        res = await self.session.execute(q)
        return [r[0] for r in res.all()]