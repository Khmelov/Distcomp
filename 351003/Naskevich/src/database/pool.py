from sqlalchemy import URL
from sqlalchemy.ext.asyncio import (
    AsyncSession,
    async_sessionmaker,
    create_async_engine,
)


def create_pool(url: str | URL, pool_size: int = 10, enable_logging: bool = False) -> async_sessionmaker[AsyncSession]:
    engine = create_async_engine(url=url, echo=enable_logging, pool_size=pool_size)
    return async_sessionmaker(engine, expire_on_commit=False)
