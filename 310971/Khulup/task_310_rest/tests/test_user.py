import pytest

pytestmark = pytest.mark.asyncio

async def test_create_user_success(client):
    response = await client.post("/api/v1.0/users", json={
        "login": "validuser",
        "password": "validpassword123",
        "firstname": "John",
        "lastname": "Doe"
    })
    assert response.status_code == 201
    data = response.json()
    assert data["login"] == "validuser"
    assert "id" in data

async def test_create_user_validation_error(client):
    response = await client.post("/api/v1.0/users", json={
        "login": "a",  
        "password": "123",  
        "firstname": "John",
        "lastname": "Doe"
    })
    assert response.status_code == 422

async def test_get_user_by_id_success(client):
    create_resp = await client.post("/api/v1.0/users", json={
        "login": "user2",
        "password": "password456",
        "firstname": "Alice",
        "lastname": "Smith"
    })
    user_id = create_resp.json()["id"]

    get_resp = await client.get(f"/api/v1.0/users/{user_id}")
    assert get_resp.status_code == 200
    assert get_resp.json()["login"] == "user2"

async def test_get_user_not_found(client):
    response = await client.get("/api/v1.0/users/9999")
    assert response.status_code == 404
    data = response.json()
    assert "errorMessage" in data
    assert "errorCode" in data

async def test_list_users(client):
    response = await client.get("/api/v1.0/users")
    assert response.status_code == 200
    assert isinstance(response.json(), list)

async def test_update_user_success(client):
    create_resp = await client.post("/api/v1.0/users", json={
        "login": "user3",
        "password": "password789",
        "firstname": "Bob",
        "lastname": "Johnson"
    })
    user_id = create_resp.json()["id"]

    update_resp = await client.put("/api/v1.0/users", json={
        "id": user_id,
        "login": "updateduser",
        "password": "newpassword123",
        "firstname": "Robert",
        "lastname": "Johnson"
    })
    assert update_resp.status_code == 200
    assert update_resp.json()["login"] == "updateduser"

async def test_update_user_not_found(client):
    response = await client.put("/api/v1.0/users", json={
        "id": 9999,
        "login": "ghost",
        "password": "ghostpass123",
        "firstname": "Ghost",
        "lastname": "User"
    })
    assert response.status_code == 404
    assert "errorMessage" in response.json()

async def test_delete_user_success(client):
    create_resp = await client.post("/api/v1.0/users", json={
        "login": "user4",
        "password": "password456",
        "firstname": "Delete",
        "lastname": "Me"
    })
    user_id = create_resp.json()["id"]

    delete_resp = await client.delete(f"/api/v1.0/users/{user_id}")
    assert delete_resp.status_code == 204

async def test_delete_user_not_found(client):
    response = await client.delete("/api/v1.0/users/9999")
    assert response.status_code == 404
    assert "errorMessage" in response.json()
