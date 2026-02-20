import pytest

@pytest.mark.asyncio
async def test_tweet_full_crud(client):
    # STEP 1. PREPARE AUTHOR FOR TWEET
    auth_resp = await client.post("/api/v1.0/authors", json={
        "login": "tweet_user", "password": "password123", 
        "firstname": "Vlada", "lastname": "Kolbeko"
    })
    assert auth_resp.status_code == 201, f"Failed to create author: {auth_resp.json()}"
    author_id = auth_resp.json()["id"]

    # STEP 2. CREATE TWEET
    tweet_payload = {
        "authorId": author_id,
        "title": "Title 1",
        "content": "Content longer than 4 chars",
        "labelIds": []
    }
    response = await client.post("/api/v1.0/tweets", json=tweet_payload)
    assert response.status_code == 201
    tweet_id = response.json()["id"]

    # STEP 3. UPDATE TWEET
    tweet_payload["title"] = "Updated Title"
    response = await client.put(f"/api/v1.0/tweets/{tweet_id}", json=tweet_payload)
    assert response.status_code == 200
    assert response.json()["title"] == "Updated Title"

    # STEP 4. ERROR CASE: VALIDATION FAILED (CODE 40000)
    tweet_payload["title"] = "T" # too short
    response = await client.post("/api/v1.0/tweets", json=tweet_payload)
    assert response.status_code == 400
    assert response.json()["errorCode"] == "40000"

    # STEP 5. DELETE TWEET
    response = await client.delete(f"/api/v1.0/tweets/{tweet_id}")
    assert response.status_code == 204