from typing import Type, TypeVar, List, Optional
from sqlalchemy import select, delete, update
from sqlalchemy.ext.asyncio import AsyncSession
from app.core.database import Base

T = TypeVar("T", bound=Base)

class BaseRepository:
    def __init__(self, model: Type[T]):
        self.model = model

    async def get_all(self, session: AsyncSession, limit: int = 10, offset: int = 0) -> List[T]:
        query = select(self.model).limit(limit).offset(offset)
        result = await session.execute(query)
        return result.scalars().all()

    async def get_by_id(self, session: AsyncSession, id: int) -> Optional[T]:
        result = await session.execute(select(self.model).filter(self.model.id == id))
        return result.scalar_one_or_none()

    async def create(self, session: AsyncSession, data: dict) -> T:
        instance = self.model(**data)
        session.add(instance)
        await session.commit()
        await session.refresh(instance)
        return instance

    async def update(self, session: AsyncSession, id: int, data: dict) -> Optional[T]:
        query = update(self.model).where(self.model.id == id).values(**data).returning(self.model)
        result = await session.execute(query)
        await session.commit()
        return result.scalar_one_or_none()

    async def delete(self, session: AsyncSession, id: int) -> bool:
        result = await session.execute(delete(self.model).where(self.model.id == id))
        await session.commit()
        return result.rowcount > 0
    
from app.models.author import Author
from app.models.tweet import Tweet
from app.models.label import Label
from app.models.notice import Notice

author_repo = BaseRepository(Author)
tweet_repo = BaseRepository(Tweet)
label_repo = BaseRepository(Label)
notice_repo = BaseRepository(Notice)