import pytest_asyncio
from httpx import AsyncClient, ASGITransport
from main import app

@pytest_asyncio.fixture(scope="function")
async def client():
    async with AsyncClient(transport=ASGITransport(app=app), base_url="http://test") as ac:
        yield ac