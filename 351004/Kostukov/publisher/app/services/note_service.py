from sqlalchemy.ext.asyncio import AsyncSession
from fastapi import HTTPException
from sqlalchemy.exc import IntegrityError
from publisher.app.core.notes.dto import NoteRequestTo
from publisher.app.infrastructure.db.repo import NoteRepo, ArticleRepo

class NoteService:
    def __init__(self):
        self.repo = NoteRepo()
        self.article_repo = ArticleRepo()

    async def create(self, session: AsyncSession, dto: NoteRequestTo):
        data = dto.model_dump(exclude_none=True)

        if "articleId" in data:
            data["article_id"] = data.pop("articleId")

        try:
            return await self.repo.create(session, data)
        except IntegrityError:
            await session.rollback()
            raise HTTPException(status_code=403, detail="Invalid articleId or data")

    async def get_all(self, session: AsyncSession, skip=0, limit=10):
        return await self.repo.get_all(session, skip=skip, limit=limit)

    async def get_by_id(self, session: AsyncSession, id: int):
        return await self.repo.get_by_id(session, id)

    async def update(self, session: AsyncSession, id: int, dto: NoteRequestTo):
        data = dto.model_dump(exclude_none=True)
        if "articleId" in data:
            data["article_id"] = data.pop("articleId")
        return await self.repo.update(session, id, data)

    async def delete(self, session: AsyncSession, id: int):
        return await self.repo.delete(session, id)