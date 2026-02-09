import pytest

@pytest.mark.asyncio
async def test_notice_full_crud(client):
    # STEP 1. PREPARE AUTHOR AND TWEET FOR NOTICE
    auth_resp = await client.post("/api/v1.0/authors", json={
        "login": "notice_user", "password": "password123", 
        "firstname": "Vlada", "lastname": "Kolbeko" # Исправлено
    })
    assert auth_resp.status_code == 201, f"Failed to create author: {auth_resp.json()}"
    author_id = auth_resp.json()["id"]
    tweet_resp = await client.post("/api/v1.0/tweets", json={
        "authorId": author_id, "title": "Tweet for notice", 
        "content": "Valid content", "labelIds": []
    })
    tweet_id = tweet_resp.json()["id"]

    # STEP 2. CREATE NOTICE
    notice_payload = {"tweetId": tweet_id, "content": "Notice message"}
    response = await client.post("/api/v1.0/notices", json=notice_payload)
    assert response.status_code == 201
    notice_id = response.json()["id"]

    # STEP 3. UPDATE NOTICE
    notice_payload["content"] = "Updated notice message"
    response = await client.put(f"/api/v1.0/notices/{notice_id}", json=notice_payload)
    assert response.status_code == 200
    assert response.json()["content"] == "Updated notice message"

    # STEP 4. ERROR CASE: TWEET NOT FOUND FOR NOTICE (CODE 40012)
    response = await client.post("/api/v1.0/notices", json={
        "tweetId": 99999, "content": "Fail content"
    })
    assert response.status_code == 400
    assert response.json()["errorCode"] == "40012"

    # STEP 5. DELETE NOTICE
    response = await client.delete(f"/api/v1.0/notices/{notice_id}")
    assert response.status_code == 204