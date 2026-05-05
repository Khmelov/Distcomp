from typing import List

from sqlalchemy.ext.asyncio import AsyncSession

from src.domain.repositories import UserRepository
from src.schemas.user import UserResponseTo, UserRequestTo
from .auth import AuthService


class UserService:
    def __init__(self, session: AsyncSession) -> None:
        self.session = session
        self.user_repo = UserRepository(session)

    async def get_one(self, user_id: int) -> UserResponseTo:
        user = await self.user_repo.get_one(user_id)
        return UserResponseTo.model_validate(user)

    async def get_all(self) -> List[UserResponseTo]:
        users = await self.user_repo.get_all()
        return [UserResponseTo.model_validate(a) for a in users]

    async def create(self, dto: UserRequestTo) -> UserResponseTo:
        dto.password = AuthService.get_hashed_password(dto.password)
        user_args = dto.model_dump()
        created_user = await self.user_repo.create(**user_args)
        await self.session.commit()
        return UserResponseTo.model_validate(created_user)

    async def update(self, user_id: int, dto: UserRequestTo) -> UserResponseTo:
        user_args = dto.model_dump()
        updated_user = await self.user_repo.update(user_id, **user_args)
        await self.session.commit()
        return UserResponseTo.model_validate(updated_user)

    async def delete(self, user_id: int) -> None:
        await self.user_repo.delete(user_id)
        await self.session.commit()