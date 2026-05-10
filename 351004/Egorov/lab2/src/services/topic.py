from typing import List

from sqlalchemy.ext.asyncio import AsyncSession

from src.domain.repositories.db import TopicRepository, TagRepository
from src.schemas.topic import TopicResponseTo, TopicRequestTo


class TopicService:
    def __init__(self, session: AsyncSession) -> None:
        self.session = session
        self.tag_repo = TagRepository(session)
        self.topic_repo = TopicRepository(session)

    async def get_one(self, topic_id: int) -> TopicResponseTo:
        topic = await self.topic_repo.get_one(topic_id)
        return TopicResponseTo.model_validate(topic)

    async def get_all(self) -> List[TopicResponseTo]:
        topics = await self.topic_repo.get_all()
        return [TopicResponseTo.model_validate(t) for t in topics]

    async def create(self, dto: TopicRequestTo) -> TopicResponseTo:
        topic_args = dto.model_dump()
        tag_names = topic_args.pop("tags", [])
        tag_objects = await self.tag_repo.get_or_create_many(tag_names)
        created_topic = await self.topic_repo.create(**topic_args, tags=tag_objects)
        await self.session.commit()
        return TopicResponseTo.model_validate(created_topic)

    async def update(self, topic_id: int, dto: TopicRequestTo) -> TopicResponseTo:
        topic_args = dto.model_dump()
        tag_names = topic_args.pop("tags", []) or []
        topic_args["tags"] = await self.tag_repo.get_or_create_many(tag_names)
        updated_topic = await self.topic_repo.update(topic_id, **topic_args)
        await self.session.commit()
        return TopicResponseTo.model_validate(updated_topic)

    async def delete(self, topic_id: int) -> None:
        await self.topic_repo.delete_with_tags(topic_id)
        await self.session.commit()