from typing import List

from sqlalchemy.ext.asyncio import AsyncSession

from src.domain.repositories import NoticeRepository, LabelRepository
from src.schemas.notice import NoticeResponseTo, NoticeRequestTo


class NoticeService:
    def __init__(self, session: AsyncSession) -> None:
        self.session = session
        self.label_repo = LabelRepository(session)
        self.notice_repo = NoticeRepository(session)

    async def get_one(self, notice_id: int) -> NoticeResponseTo:
        notice = await self.notice_repo.get_one(notice_id)
        return NoticeResponseTo.model_validate(notice)

    async def get_all(self) -> List[NoticeResponseTo]:
        notices = await self.notice_repo.get_all()
        return [NoticeResponseTo.model_validate(t) for t in notices]

    async def create(self, dto: NoticeRequestTo) -> NoticeResponseTo:
        notice_args = dto.model_dump()
        label_names = notice_args.pop("labels", [])
        label_objects = await self.label_repo.get_or_create_many(label_names)
        created_notice = await self.notice_repo.create(**notice_args, labels=label_objects)
        await self.session.commit()
        return NoticeResponseTo.model_validate(created_notice)

    async def update(self, notice_id: int, dto: NoticeRequestTo) -> NoticeResponseTo:
        notice_args = dto.model_dump()
        label_names = notice_args.pop("labels", []) or []
        notice_args["labels"] = await self.label_repo.get_or_create_many(label_names)
        updated_notice = await self.notice_repo.update(notice_id, **notice_args)
        await self.session.commit()
        return NoticeResponseTo.model_validate(updated_notice)

    async def delete(self, notice_id: int) -> None:
        await self.notice_repo.delete_with_labels(notice_id)
        await self.session.commit()