import pytest
import pytest_asyncio
from httpx import AsyncClient

pytestmark = pytest.mark.asyncio

@pytest_asyncio.fixture
async def client():
    async with AsyncClient(base_url="http://localhost:24110") as ac:
        yield ac
