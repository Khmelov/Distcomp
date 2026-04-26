from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from app.models.label import Label
from app.schemas.label import LabelRequestTo, LabelResponseTo
from app.core.exceptions import AppException
from app.repository.db import label_repo

class LabelService:
    async def get_or_create(self, session: AsyncSession, name: str) -> Label:
        result = await session.execute(select(Label).where(Label.name == name))
        label = result.scalar_one_or_none()
        if not label:
            label = Label(name=name)
            session.add(label)
            await session.flush()
        return label

    async def create(self, session: AsyncSession, dto: LabelRequestTo) -> LabelResponseTo:
        label = await label_repo.create(session, dto.model_dump())
        return LabelResponseTo.model_validate(label, from_attributes=True)

    async def get_all(self, session: AsyncSession, page: int = 1, size: int = 100):
        labels = await label_repo.get_all(session, limit=size, offset=(page - 1) * size)
        return [LabelResponseTo.model_validate(l, from_attributes=True) for l in labels]

    async def get_by_id(self, session: AsyncSession, id: int):
        res = await label_repo.get_by_id(session, id)
        if not res: raise AppException(404, "Label not found", 9)
        return LabelResponseTo.model_validate(res, from_attributes=True)

    async def update(self, session: AsyncSession, id: int, dto: LabelRequestTo) -> LabelResponseTo:
        updated = await label_repo.update(session, id, dto.model_dump(exclude={'id'}))
        if not updated: raise AppException(404, "Label not found", 10)
        return LabelResponseTo.model_validate(updated, from_attributes=True)

    async def delete(self, session: AsyncSession, id: int):
        if not await label_repo.delete(session, id):
            raise AppException(404, "Label not found", 11)