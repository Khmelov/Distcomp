from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from src.domain.models import Notice, Label, User
from src.domain.repositories.postgresql import SqlAlchemyRepository


class NoticeRepository(SqlAlchemyRepository[Notice]):
    def __init__(self, session: AsyncSession):
        super().__init__(session, Notice)

    async def delete_with_labels(self, entity_id: int) -> None:
        result = await self.session.execute(
            select(Notice)
            .where(Notice.id == entity_id)
        )
        notice = result.scalar_one_or_none()

        for label in notice.labels:
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

class UserRepository(SqlAlchemyRepository[User]):
    def __init__(self, session: AsyncSession):
        super().__init__(session, User)

    async def get_by_login(self, login: str) -> User | None:
        query = select(User).where(User.login == login)
        result = await self.session.execute(query)
        return result.scalar_one_or_none()