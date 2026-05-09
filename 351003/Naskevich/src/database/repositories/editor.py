from sqlalchemy import select, delete

from src.database.repositories.base import SQLAlchemyRepository
from src.database.tables import editors_table
from src.models.editor import Editor
from src.repositories.editor import AbstractEditorRepository


class EditorRepository(SQLAlchemyRepository, AbstractEditorRepository):

    async def get_by_id(self, entity_id: int) -> Editor | None:
        result = await self._session.execute(
            select(Editor).where(editors_table.c.id == entity_id)
        )
        return result.scalar_one_or_none()

    async def get_all(self) -> list[Editor]:
        result = await self._session.execute(select(Editor))
        return list(result.scalars().all())

    async def create(self, entity: Editor) -> Editor:
        self._session.add(entity)
        await self._session.flush()
        await self._session.refresh(entity)
        return entity

    async def update(self, entity: Editor) -> Editor | None:
        existing = await self.get_by_id(entity.id)
        if existing is None:
            return None
        existing.login = entity.login
        existing.password = entity.password
        existing.firstname = entity.firstname
        existing.lastname = entity.lastname
        existing.role = entity.role
        await self._session.flush()
        await self._session.refresh(existing)
        return existing

    async def delete(self, entity_id: int) -> bool:
        result = await self._session.execute(
            delete(Editor).where(editors_table.c.id == entity_id)
        )
        return result.rowcount > 0

    async def get_by_login(self, login: str) -> Editor | None:
        result = await self._session.execute(
            select(Editor).where(editors_table.c.login == login)
        )
        return result.scalar_one_or_none()
