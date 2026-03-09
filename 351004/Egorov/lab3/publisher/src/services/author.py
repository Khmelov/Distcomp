from typing import List

from sqlalchemy.ext.asyncio import AsyncSession

from src.domain.repositories.postgresql import AuthorRepository
from src.schemas.author import AuthorResponseTo, AuthorRequestTo


class AuthorService:
    def __init__(self, session: AsyncSession) -> None:
        self.session = session
        self.author_repo = AuthorRepository(session)

    async def get_one(self, author_id: int) -> AuthorResponseTo:
        author = await self.author_repo.get_one(author_id)
        return AuthorResponseTo.model_validate(author)

    async def get_all(self) -> List[AuthorResponseTo]:
        authors = await self.author_repo.get_all()
        return [AuthorResponseTo.model_validate(a) for a in authors]

    async def create(self, dto: AuthorRequestTo) -> AuthorResponseTo:
        user_args = dto.model_dump()
        created_author = await self.author_repo.create(**user_args)
        await self.session.commit()
        return AuthorResponseTo.model_validate(created_author)

    async def update(self, author_id: int, dto: AuthorRequestTo) -> AuthorResponseTo:
        user_args = dto.model_dump()
        updated_author = await self.author_repo.update(author_id, **user_args)
        await self.session.commit()
        return AuthorResponseTo.model_validate(updated_author)

    async def delete(self, author_id: int) -> None:
        await self.author_repo.delete(author_id)
        await self.session.commit()