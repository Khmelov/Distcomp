from sqlalchemy.ext.asyncio import create_async_engine, async_sessionmaker, AsyncSession
from sqlalchemy.orm import declarative_base

from Task320.src.core.config import settings

engine = create_async_engine(settings.database_url, echo=True)
AsyncSessionLocal = async_sessionmaker(engine, expire_on_commit=False)

Base = declarative_base()

async def get_db() -> AsyncSession:
    async with AsyncSessionLocal() as session:
        try:
            yield session
            await session.commit()  # Фиксируем транзакцию после успешного ответа
        except Exception:
            await session.rollback()  # Откатываем при ошибке
            raise
        finally:
            await session.close()  # Явно закрываем сессию (опционально)