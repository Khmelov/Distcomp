import pytest
from httpx import AsyncClient
from sqlalchemy import text
from sqlalchemy.ext.asyncio import create_async_engine, AsyncSession
from sqlalchemy.orm import sessionmaker
from testcontainers.postgres import PostgresContainer

from Task320.main import app
from Task320.src.infrastructure.database import Base, get_db
from Task320.src.core.config import settings


@pytest.fixture(scope="session")
def postgres_container():
    with PostgresContainer("postgres:15", driver="asyncpg") as postgres:
        settings.DB_HOST = postgres.get_container_host_ip()
        settings.DB_PORT = postgres.get_exposed_port(5432)
        settings.DB_USER = postgres.USER
        settings.DB_PASSWORD = postgres.PASSWORD
        settings.DB_NAME = postgres.DBNAME
        yield postgres


@pytest.fixture(scope="session")
async def engine(postgres_container):
    engine = create_async_engine(settings.database_url, echo=True)
    async with engine.begin() as conn:
        await conn.execute(text("CREATE SCHEMA IF NOT EXISTS distcomp"))
        await conn.execute(text("ALTER USER postgres SET search_path TO distcomp, public"))
        await conn.run_sync(Base.metadata.create_all)
        # Вставка маркеров УДАЛЕНА
    yield engine
    await engine.dispose()


@pytest.fixture
async def session(engine):
    async_session = sessionmaker(engine, class_=AsyncSession, expire_on_commit=False)
    async with async_session() as s:
        await s.begin()
        try:
            yield s
        finally:
            await s.rollback()


@pytest.fixture
async def client(session):
    async def override_get_db():
        yield session

    app.dependency_overrides[get_db] = override_get_db
    async with AsyncClient(app=app, base_url="http://test") as ac:
        yield ac
    app.dependency_overrides.clear()