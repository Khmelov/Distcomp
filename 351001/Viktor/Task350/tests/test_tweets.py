import pytest
from httpx import AsyncClient

pytestmark = pytest.mark.asyncio

async def test_create_tweet(client: AsyncClient):
    # Сначала создаём creator
    cr = await client.post("/api/v1.0/creators", json={
        "login": "tweetuser",
        "password": "pass",
        "firstname": "A",
        "lastname": "B"
    })
    creator_id = cr.json()["id"]
    payload = {
        "title": "My first tweet",
        "content": "Hello world!",
        "creator_id": creator_id
    }
    resp = await client.post("/api/v1.0/tweets", json=payload)
    assert resp.status_code == 201
    data = resp.json()
    assert data["id"] is not None
    assert data["title"] == "My first tweet"