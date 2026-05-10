from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from src.domain.models import News, Label, Writer
from src.domain.repositories.postgresql import SqlAlchemyRepository


class NewsRepository(SqlAlchemyRepository[News]):
    def __init__(self, session: AsyncSession):
        super().__init__(session, News)

    async def delete_with_labels(self, entity_id: int) -> None:
        result = await self.session.execute(
            select(News)
            .where(News.id == entity_id)
        )
        news = result.scalar_one_or_none()

        for label in news.labels:
            await self.session.delete(label)

        await self.delete(entity_id)

class LabelRepository(SqlAlchemyRepository[Label]):
    def __init__(self, session: AsyncSession):
        super().__init__(session, Label)

    async def get_or_create_many(self, label_names: list[str]) -> list[Label]:
        if not label_names:
            return []

        result = await self.session.execute(
            select(self.model_class).where(self.model_class.name.in_(label_names))
        )
        existing_labels = result.scalars().all()

        existing_names = {label.name for label in existing_labels}
        missing_names = {
            name
            for name in label_names
            if name not in existing_names
        }

        new_labels = [self.model_class(name=name) for name in missing_names]

        if new_labels:
            self.session.add_all(new_labels)
            await self.session.flush()

        return list(existing_labels) + new_labels

class WriterRepository(SqlAlchemyRepository[Writer]):
    def __init__(self, session: AsyncSession):
        super().__init__(session, Writer)

    async def get_by_login(self, login: str) -> Writer | None:
        query = select(Writer).where(Writer.login == login)
        result = await self.session.execute(query)
        return result.scalar_one_or_none()