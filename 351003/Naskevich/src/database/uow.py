from abc import ABC, abstractmethod

from sqlalchemy.ext.asyncio import AsyncSession


class UnitOfWork(ABC):

    @abstractmethod
    async def commit(self) -> None:
        raise NotImplementedError

    @abstractmethod
    async def rollback(self) -> None:
        raise NotImplementedError


class SQLAlchemyUoW(UnitOfWork):
    def __init__(self, session: AsyncSession) -> None:
        self._session = session

    async def commit(self) -> None:
        await self._session.commit()

    async def rollback(self) -> None:
        await self._session.rollback()
