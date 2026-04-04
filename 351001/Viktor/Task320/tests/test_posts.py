import pytest
from httpx import AsyncClient

pytestmark = pytest.mark.asyncio

async def test_create_post(client: AsyncClient):
    # Создаём твит
    cr = await client.post("/api/v1.0/creators", json={
        "login": "postuser",
        "password": "pass",
        "firstname": "P",
        "lastname": "O"
    })
    tweet = await client.post("/api/v1.0/tweets", json={
        "title": "Tweet for post",
        "content": "content",
        "creator_id": cr.json()["id"]
    })
    tweet_id = tweet.json()["id"]
    payload = {
        "content": "This is a post",
        "tweet_id": tweet_id
    }
    resp = await client.post("/api/v1.0/posts", json=payload)
    assert resp.status_code == 201
    data = resp.json()
    assert data["id"] is not None
    assert data["content"] == "This is a post"

async def test_get_posts_by_tweet(client: AsyncClient):
    # Создаём твит и пост
    cr = await client.post("/api/v1.0/creators", json={
        "login": "postuser2",
        "password": "pass",
        "firstname": "P2",
        "lastname": "O2"
    })
    tweet = await client.post("/api/v1.0/tweets", json={
        "title": "Tweet for posts",
        "content": "content",
        "creator_id": cr.json()["id"]
    })
    tweet_id = tweet.json()["id"]
    await client.post("/api/v1.0/posts", json={"content": "Post 1", "tweet_id": tweet_id})
    await client.post("/api/v1.0/posts", json={"content": "Post 2", "tweet_id": tweet_id})
    resp = await client.get(f"/api/v1.0/posts/by_tweet/{tweet_id}")
    assert resp.status_code == 200
    data = resp.json()
    assert len(data) == 2