import pytest_asyncio
import pytest
from httpx import AsyncClient, ASGITransport
import sqlalchemy
from sqlalchemy.ext.asyncio import create_async_engine, AsyncSession
from sqlalchemy.orm import sessionmaker
from testcontainers.postgres import PostgresContainer
from main import app
from app.core.database import Base, get_db

@pytest.fixture(scope="session")
def postgres_container():
    with PostgresContainer("postgres:16-alpine") as postgres:
        db_url = f"postgresql+asyncpg://{postgres.username}:{postgres.password}@{postgres.get_container_host_ip()}:{postgres.get_exposed_port(5432)}/{postgres.dbname}"
        yield db_url

@pytest_asyncio.fixture(scope="function")
async def db_session(postgres_container):
    engine = create_async_engine(postgres_container, echo=False)
    
    async with engine.connect() as conn:
        await conn.execute(sqlalchemy.text("CREATE SCHEMA IF NOT EXISTS distcomp"))
        await conn.execute(sqlalchemy.text("SET search_path TO distcomp"))
        await conn.commit()

    async with engine.begin() as conn:
        await conn.run_sync(Base.metadata.create_all)
    
    async_session = sessionmaker(engine, class_=AsyncSession, expire_on_commit=False)
    
    async with async_session() as session:
        await session.execute(sqlalchemy.text("SET search_path TO distcomp"))
        yield session
        await session.rollback()

@pytest_asyncio.fixture(scope="function")
async def client(db_session):
    app.dependency_overrides[get_db] = lambda: db_session
    
    async with AsyncClient(transport=ASGITransport(app=app), base_url="http://test") as ac:
        yield ac
    
    app.dependency_overrides.clear()