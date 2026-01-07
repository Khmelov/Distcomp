import pytest
import pytest_asyncio

pytestmark = pytest.mark.asyncio

@pytest_asyncio.fixture
async def created_user(client):
    resp = await client.post("/api/v1.0/users", json={
        "login": "issueuser",
        "password": "pass123456",
        "firstname": "Issue",
        "lastname": "Owner"
    })
    return resp.json()

async def test_create_issue_success(client, created_user):
    response = await client.post("/api/v1.0/issues", json={
        "userId": created_user["id"],
        "title": "Title",
        "content": "Some content"
    })
    assert response.status_code == 201
    data = response.json()
    assert data["title"] == "Title"
    assert data["userId"] == created_user["id"]
    assert "id" in data

async def test_create_issue_validation_error(client):
    response = await client.post("/api/v1.0/issues", json={
        "userId": 1,
        "title": "T",           
        "content": ""           
    })
    assert response.status_code == 422

async def test_get_all_issues(client, created_user):
    await client.post("/api/v1.0/issues", json={
        "userId": created_user["id"],
        "title": "Issue A",
        "content": "Content A"
    })
    response = await client.get("/api/v1.0/issues")
    assert response.status_code == 200
    assert isinstance(response.json(), list)

async def test_get_issue_by_id(client, created_user):
    create_resp = await client.post("/api/v1.0/issues", json={
        "userId": created_user["id"],
        "title": "Unique Issue",
        "content": "Some details"
    })
    issue_id = create_resp.json()["id"]

    get_resp = await client.get(f"/api/v1.0/issues/{issue_id}")
    assert get_resp.status_code == 200
    assert get_resp.json()["title"] == "Unique Issue"

async def test_get_issue_not_found(client):
    response = await client.get("/api/v1.0/issues/9999")
    assert response.status_code == 404
    data = response.json()
    assert "errorMessage" in data
    assert "errorCode" in data

async def test_update_issue_success(client, created_user):
    create_resp = await client.post("/api/v1.0/issues", json={
        "userId": created_user["id"],
        "title": "Old Title",
        "content": "Old content"
    })
    issue_id = create_resp.json()["id"]

    update_resp = await client.put("/api/v1.0/issues", json={
        "id": issue_id,
        "userId": created_user["id"],
        "title": "New Title",
        "content": "Updated content"
    })
    assert update_resp.status_code == 200
    assert update_resp.json()["title"] == "New Title"

async def test_update_issue_not_found(client, created_user):
    response = await client.put("/api/v1.0/issues", json={
        "id": 9999,
        "userId": created_user["id"],
        "title": "Doesn't Exist",
        "content": "No such issue"
    })
    assert response.status_code == 404
    data = response.json()
    assert "errorMessage" in data

async def test_delete_issue_success(client, created_user):
    create_resp = await client.post("/api/v1.0/issues", json={
        "userId": created_user["id"],
        "title": "To be deleted",
        "content": "Temp content"
    })
    issue_id = create_resp.json()["id"]

    delete_resp = await client.delete(f"/api/v1.0/issues/{issue_id}")
    assert delete_resp.status_code == 204

async def test_delete_issue_not_found(client):
    response = await client.delete("/api/v1.0/issues/9999")
    assert response.status_code == 404
    data = response.json()
    assert "errorMessage" in data
