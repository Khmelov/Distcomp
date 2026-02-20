import pytest

@pytest.mark.asyncio
async def test_author_full_crud(client):
    # STEP 1. CREATE AUTHOR
    payload = {
        "login": "author_test",
        "password": "password123",
        "firstname": "Ivan",
        "lastname": "Ivanov"
    }
    response = await client.post("/api/v1.0/authors", json=payload)
    assert response.status_code == 201
    author_id = response.json()["id"]

    # STEP 2. GET AUTHOR BY ID
    response = await client.get(f"/api/v1.0/authors/{author_id}")
    assert response.status_code == 200
    assert response.json()["login"] == "author_test"

    # STEP 3. UPDATE AUTHOR
    payload["firstname"] = "Petr"
    response = await client.put(f"/api/v1.0/authors/{author_id}", json=payload)
    assert response.status_code == 200
    assert response.json()["firstname"] == "Petr"

    # STEP 4. DELETE AUTHOR
    response = await client.delete(f"/api/v1.0/authors/{author_id}")
    assert response.status_code == 204

    # STEP 5. ERROR CASE: AUTHOR NOT FOUND (CODE 40401)
    response = await client.get(f"/api/v1.0/authors/{author_id}")
    assert response.status_code == 404
    assert response.json()["errorCode"] == "40401"