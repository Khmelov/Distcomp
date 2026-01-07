import pytest
import pytest_asyncio

pytestmark = pytest.mark.asyncio

@pytest_asyncio.fixture
async def created_user(client):
    response = await client.post("/api/v1.0/users", json={
        "login": "testuser",
        "password": "password123",
        "firstname": "Test",
        "lastname": "User"
    })
    return response.json()

@pytest_asyncio.fixture
async def created_issue(client, created_user):
    response = await client.post("/api/v1.0/issues", json={
        "userId": created_user["id"],
        "title": "Test Issue",
        "content": "Issue content"
    })
    return response.json()

@pytest_asyncio.fixture
async def created_note(client, created_issue):
    response = await client.post("/api/v1.0/notes", json={
        "issueId": created_issue["id"],
        "content": "Note content"
    })
    return response.json()


async def test_create_note_success(client, created_issue):
    response = await client.post("/api/v1.0/notes", json={
        "issueId": created_issue["id"],
        "content": "New note content"
    })
    assert response.status_code == 201
    data = response.json()
    assert "id" in data
    assert data["issueId"] == created_issue["id"]
    assert data["content"] == "New note content"


async def test_create_note_validation_error(client):
    response = await client.post("/api/v1.0/notes", json={
        "issueId": 1,
        "content": ""
    })
    assert response.status_code == 422


async def test_get_all_notes(client, created_note):
    await client.post("/api/v1.0/notes", json={
        "issueId": created_note["issueId"],
        "content": "Second note"
    })
    response = await client.get("/api/v1.0/notes")
    assert response.status_code == 200
    data = response.json()
    assert isinstance(data, list)
    assert len(data) >= 2
    assert any(note["content"] == "Second note" for note in data)


async def test_get_note_by_id(client, created_note):
    response = await client.get(f"/api/v1.0/notes/{created_note['id']}")
    assert response.status_code == 200
    data = response.json()
    assert data["id"] == created_note["id"]
    assert data["content"] == created_note["content"]


async def test_get_note_by_id_not_found(client):
    response = await client.get("/api/v1.0/notes/999999")
    assert response.status_code == 404
    data = response.json()
    assert "errorMessage" in data
    assert "errorCode" in data
    assert str(data["errorCode"]).startswith("404")


async def test_update_note_success(client, created_note):
    response = await client.put("/api/v1.0/notes", json={
        "id": created_note["id"],
        "issueId": created_note["issueId"],
        "content": "Updated note content"
    })
    assert response.status_code == 200
    data = response.json()
    assert data["id"] == created_note["id"]
    assert data["content"] == "Updated note content"


async def test_update_note_not_found(client):
    response = await client.put("/api/v1.0/notes", json={
        "id": 999999,
        "issueId": 1,
        "content": "Doesn't exist"
    })
    assert response.status_code == 404
    data = response.json()
    assert "errorMessage" in data
    assert "errorCode" in data
    assert str(data["errorCode"]).startswith("404")


async def test_delete_note_success(client, created_note):
    response = await client.delete(f"/api/v1.0/notes/{created_note['id']}")
    assert response.status_code == 204

    get_resp = await client.get(f"/api/v1.0/notes/{created_note['id']}")
    assert get_resp.status_code == 404


async def test_delete_note_not_found(client):
    response = await client.delete("/api/v1.0/notes/999999")
    assert response.status_code == 404
    data = response.json()
    assert "errorMessage" in data
    assert "errorCode" in data
    assert str(data["errorCode"]).startswith("404")
