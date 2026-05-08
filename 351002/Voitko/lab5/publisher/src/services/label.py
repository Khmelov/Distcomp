from typing import List

from sqlalchemy.ext.asyncio import AsyncSession

from src.domain.repositories import LabelRepository
from src.schemas.label import LabelResponseTo, LabelRequestTo


class LabelService:
    def __init__(self, session: AsyncSession) -> None:
        self.session = session
        self.label_repo = LabelRepository(session)

    async def get_one(self, label_id: int) -> LabelResponseTo:
        label = await self.label_repo.get_one(label_id)
        return LabelResponseTo.model_validate(label)

    async def get_all(self) -> List[LabelResponseTo]:
        labels = await self.label_repo.get_all()
        return [LabelResponseTo.model_validate(t) for t in labels]

    async def create(self, dto: LabelRequestTo) -> LabelResponseTo:
        label_args = dto.model_dump()
        created_label = await self.label_repo.create(**label_args)
        await self.session.commit()
        return LabelResponseTo.model_validate(created_label)

    async def update(self, label_id: int, dto: LabelRequestTo) -> LabelResponseTo:
        label_args = dto.model_dump()
        updated_label = await self.label_repo.update(label_id, **label_args)
        await self.session.commit()
        return LabelResponseTo.model_validate(updated_label)

    async def delete(self, label_id: int) -> None:
        await self.label_repo.delete(label_id)
        await self.session.commit()
