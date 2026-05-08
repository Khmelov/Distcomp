from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession
from app.repository.db import author_repo
from app.models.author import Author
from app.schemas.author import AuthorRequestTo, AuthorResponseTo
from app.core.exceptions import AppException

class AuthorService:
    async def create(self, session: AsyncSession, dto: AuthorRequestTo) -> AuthorResponseTo:
        query = select(Author).where(Author.login == dto.login)
        result = await session.execute(query)
        existing_author = result.scalar_one_or_none()
        
        if existing_author:
            raise AppException(403, "Login already exists", 17)

        # Если логин свободен, создаем
        author = await author_repo.create(session, dto.model_dump())
        return AuthorResponseTo.model_validate(author, from_attributes=True)

    async def get_all(self, session: AsyncSession, page: int = 1, size: int = 100):
        authors = await author_repo.get_all(session, limit=size, offset=(page - 1) * size)
        return [AuthorResponseTo.model_validate(a, from_attributes=True) for a in authors]

    async def get_by_id(self, session: AsyncSession, id: int):
        res = await author_repo.get_by_id(session, id)
        if not res: raise AppException(404, "Author not found", 1)
        return AuthorResponseTo.model_validate(res, from_attributes=True)

    async def update(self, session: AsyncSession, id: int, dto: AuthorRequestTo) -> AuthorResponseTo:
        data = dto.model_dump(exclude={'id'})
        updated = await author_repo.update(session, id, data)
        if not updated: raise AppException(404, "Author not found", 2)
        return AuthorResponseTo.model_validate(updated, from_attributes=True)

    async def delete(self, session: AsyncSession, id: int):
        if not await author_repo.delete(session, id):
            raise AppException(404, "Author not found", 3)