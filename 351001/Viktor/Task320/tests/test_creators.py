import pytest
from httpx import AsyncClient

pytestmark = pytest.mark.asyncio

async def test_create_creator(client: AsyncClient):
    payload = {
        "login": "testuser",
        "password": "secret123",
        "firstname": "Test",
        "lastname": "User"
    }
    resp = await client.post("/api/v1.0/creators", json=payload)
    assert resp.status_code == 201
    data = resp.json()
    assert data["id"] is not None
    assert data["login"] == "testuser"

async def test_get_creators_paginated(client: AsyncClient):
    # Сначала создадим несколько
    for i in range(3):
        await client.post("/api/v1.0/creators", json={
            "login": f"user{i}",
            "password": "pass",
            "firstname": "F",
            "lastname": "L"
        })
    resp = await client.get("/api/v1.0/creators?page=1&size=2")
    assert resp.status_code == 200
    data = resp.json()
    assert len(data) == 2

async def test_get_creator_by_tweet(client: AsyncClient):
    # Создаём creator
    cr = await client.post("/api/v1.0/creators", json={
        "login": "tweetowner",
        "password": "pass",
        "firstname": "John",
        "lastname": "Doe"
    })
    creator_id = cr.json()["id"]
    # Создаём tweet
    tweet = await client.post("/api/v1.0/tweets", json={
        "title": "Test",
        "content": "Content",
        "creator_id": creator_id
    })
    tweet_id = tweet.json()["id"]
    # Получаем creator по tweet id
    resp = await client.get(f"/api/v1.0/creators/by_tweet/{tweet_id}")
    assert resp.status_code == 200
    data = resp.json()
    assert data["id"] == creator_id