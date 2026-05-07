# Импорт Annotated для создания типа с аннотациями (используется для dependency injection)
from typing import Annotated

# Импорт Depends — механизм внедрения зависимостей FastAPI
from fastapi import Depends
# Импорт AsyncSession — тип асинхронной сессии SQLAlchemy
from sqlalchemy.ext.asyncio import AsyncSession

# Импорт фабрики сессий для PostgreSQL
from database import AsyncSessionLocal


# Асинхронный генератор, предоставляющий сессию БД для каждого запроса
# Паттерн "Unit of Work": одна сессия на один HTTP-запрос
async def get_db() -> AsyncSession:
    # Создаем новую сессию и используем её как контекстный менеджер
    # При выходе из контекста сессия автоматически закрывается
    async with AsyncSessionLocal() as session:
        # yield превращает функцию в генератор — FastAPI использует это для DI
        # Сессия "отдается" в обработчик запроса, а после ответа — закрывается
        yield session


# Тип-зависимость: AsyncSession, которая автоматически создается через get_db
# Используется в параметрах обработчиков: async def get_writers(db: db_dependency)
# FastAPI автоматически вызовет get_db() и передаст результат в параметр db
db_dependency = Annotated[AsyncSession, Depends(get_db)]
