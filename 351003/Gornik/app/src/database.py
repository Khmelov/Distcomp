# Импорт AsyncSession для асинхронных сессий БД и create_async_engine для создания движка
from sqlalchemy.ext.asyncio import AsyncSession, create_async_engine
# Импорт declarative_base — фабрика для базового класса ORM-моделей
# Импорт sessionmaker — фабрика для создания сессий БД
from sqlalchemy.orm import declarative_base, sessionmaker

# Импорт класса Settings для чтения настроек подключения к PostgreSQL
from config.settings import Settings

# Создание экземпляра настроек (читает переменные окружения: POSTGRES_USER, POSTGRES_PASSWORD и т.д.)
settings = Settings()

# Формирование строки подключения к PostgreSQL
# postgresql+asyncpg — означает: протокол PostgreSQL через асинхронный драйвер asyncpg
# Формат: postgresql+asyncpg://пользователь:пароль@хост:порт/имя_базы
DB_URL = f"postgresql+asyncpg://{settings.postgres_user}:{settings.postgres_password}@{settings.postgres_host}:{settings.postgres_port}/{settings.postgres_db}"

# Создание движка SQLAlchemy — управляет пулом соединений к PostgreSQL
# Это точка входа для всех операций с базой данных
engine = create_async_engine(DB_URL)

# Создание фабрики сессий — каждый вызов AsyncSessionLocal() создает новую сессию
AsyncSessionLocal = sessionmaker(
    bind=engine,              # Привязка к движку (какая база данных)
    class_=AsyncSession,      # Тип сессии — асинхронная (для работы с async/await)
    expire_on_commit=False,   # Объекты НЕ "устаревают" после commit (можно читать атрибуты)
    autoflush=False,          # Не сбрасывать изменения автоматически перед каждым запросом
    autocommit=False,         # Транзакции управляются вручную (нужно вызывать commit())
)

# Создание базового класса для всех ORM-моделей (Writer, Tweet, Sticker наследуются от него)
# Все модели, наследующие от Base, автоматически регистрируют свои таблицы в Base.metadata
Base = declarative_base()
