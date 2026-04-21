# app/repositories/writer_repo.py
from typing import List, Dict, Any, Optional, Tuple
from sqlalchemy import select, asc, desc
from sqlalchemy.ext.asyncio import AsyncSession
from app.infrastructure.db.models import Writer as WriterModel
from app.repositories.base import BaseRepository

class WriterRepository(BaseRepository[WriterModel]):
    def __init__(self, session: AsyncSession):
        self.session = session

    async def create(self, obj: WriterModel) -> WriterModel:
        self.session.add(obj)
        await self.session.flush()
        await self.session.commit()
        await self.session.refresh(obj)
        return obj

    async def get_by_id(self, id: int) -> Optional[WriterModel]:
        q = select(WriterModel).where(WriterModel.id == id)
        res = await self.session.execute(q)
        return res.scalar_one_or_none()

    async def get_by_login(self, login: str) -> Optional[WriterModel]:
        q = select(WriterModel).where(WriterModel.login == login)
        res = await self.session.execute(q)
        return res.scalar_one_or_none()

    async def update(self, id: int, obj: WriterModel) -> WriterModel:
        existing = await self.get_by_id(id)
        if not existing:
            raise KeyError("not found")
        existing.login = obj.login
        existing.password = obj.password
        existing.firstname = obj.firstname
        existing.lastname = obj.lastname
        await self.session.commit()
        await self.session.refresh(existing)
        return existing

    async def delete(self, id: int) -> None:
        existing = await self.get_by_id(id)
        if not existing:
            raise KeyError("not found")
        await self.session.delete(existing)
        await self.session.commit()

    async def list(self, filters: Dict[str, Any] | None = None, page: int = 1, size: int = 20, sort: List[Tuple[str,str]] | None = None) -> List[WriterModel]:
        q = select(WriterModel)
        if filters:
            if "login" in filters:
                q = q.where(WriterModel.login == filters["login"])
            if "firstname" in filters:
                q = q.where(WriterModel.firstname.ilike(f"%{filters['firstname']}%"))
            if "lastname" in filters:
                q = q.where(WriterModel.lastname.ilike(f"%{filters['lastname']}%"))
        if sort:
            for field, direction in sort:
                col = getattr(WriterModel, field, None)
                if col is not None:
                    q = q.order_by(desc(col) if direction.lower()=="desc" else asc(col))
        q = q.offset((page-1)*size).limit(size)
        res = await self.session.execute(q)
        return res.scalars().all()