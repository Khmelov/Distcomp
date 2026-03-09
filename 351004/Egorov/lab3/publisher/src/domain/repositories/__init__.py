from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from src.domain.models import Topic, Tag, Note, Author
from src.domain.repositories.postgresql import SqlAlchemyRepository


class TopicRepository(SqlAlchemyRepository[Topic]):
    def __init__(self, session: AsyncSession):
        super().__init__(session, Topic)

    async def delete_with_tags(self, entity_id: int) -> None:
        result = await self.session.execute(
            select(Topic)
            .where(Topic.id == entity_id)
        )
        topic = result.scalar_one_or_none()

        for tag in topic.tags:
            await self.session.delete(tag)

        await self.delete(entity_id)

class TagRepository(SqlAlchemyRepository[Tag]):
    def __init__(self, session: AsyncSession):
        super().__init__(session, Tag)

    async def get_or_create_many(self, tag_names: list[str]) -> list[Tag]:
        if not tag_names:
            return []

        result = await self.session.execute(
            select(self.model_class).where(self.model_class.name.in_(tag_names))
        )
        existing_tags = result.scalars().all()

        existing_names = {tag.name for tag in existing_tags}
        missing_names = {
            name
            for name in tag_names
            if name not in existing_names
        }

        new_tags = [self.model_class(name=name) for name in missing_names]

        if new_tags:
            self.session.add_all(new_tags)
            await self.session.flush()

        return list(existing_tags) + new_tags

class NoteRepository(SqlAlchemyRepository[Note]):
    def __init__(self, session: AsyncSession):
        super().__init__(session, Note)

class AuthorRepository(SqlAlchemyRepository[Author]):
    def __init__(self, session: AsyncSession):
        super().__init__(session, Author)