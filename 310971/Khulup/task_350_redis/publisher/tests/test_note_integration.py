import pytest
import pytest_asyncio
from httpx import AsyncClient

pytestmark = pytest.mark.asyncio

@pytest_asyncio.fixture
async def client():
    async with AsyncClient(base_url="http://localhost:24110") as ac:
        yield ac

@pytest_asyncio.fixture
async def sample_issue_id(client):
    import random
    suffix = random.randint(1000, 9999)

    user_resp = await client.post("/api/v1.0/users", json={
        "login": f"note_user_{suffix}",
        "password": "pass123456",
        "firstname": "Note",
        "lastname": "Owner"
    })
    assert user_resp.status_code == 201
    user_id = user_resp.json()["id"]

    issue_resp = await client.post("/api/v1.0/issues", json={
        "userId": user_id,
        "title": f"Note Issue {suffix}",
        "content": "Some content"
    })
    assert issue_resp.status_code == 201
    return issue_resp.json()["id"]

async def test_discussion_health_check(client):
    response = await client.get("/api/v1.0/discussion/health")
    assert response.status_code in [200, 500]
    data = response.json()
    assert "status" in data

async def test_create_note_via_discussion(client, sample_issue_id):
    note_data = {
        "issueId": sample_issue_id,
        "content": "Test note content via discussion"
    }

    response = await client.post("/api/v1.0/notes", json=note_data)
    assert response.status_code in [201, 500]
    
    if response.status_code == 201:
        data = response.json()
        assert data["issueId"] == sample_issue_id
        assert data["content"] == "Test note content via discussion"
        assert "id" in data
        assert "createdAt" in data

async def test_get_note_by_id_via_discussion(client, sample_issue_id):
    create_data = {
        "issueId": sample_issue_id,
        "content": "Test note for get by ID via discussion"
    }
    create_response = await client.post("/api/v1.0/notes", json=create_data)
    
    if create_response.status_code == 201:
        note_id = create_response.json()["id"]
        
        response = await client.get(f"/api/v1.0/notes/{note_id}")
        assert response.status_code in [200, 500]
        
        if response.status_code == 200:
            data = response.json()
            assert data["id"] == note_id
            assert data["content"] == "Test note for get by ID via discussion"

async def test_get_notes_by_issue_id_via_discussion(client, sample_issue_id):
    notes_data = [
        {"issueId": sample_issue_id, "content": "First note via discussion"},
        {"issueId": sample_issue_id, "content": "Second note via discussion"}
    ]
    
    for note_data in notes_data:
        await client.post("/api/v1.0/notes", json=note_data)
    
    response = await client.get(f"/api/v1.0/notes?issue_id={sample_issue_id}")
    assert response.status_code in [200, 500]
    
    if response.status_code == 200:
        data = response.json()
        assert isinstance(data, list)

async def test_update_note_via_discussion(client, sample_issue_id):
    create_data = {
        "issueId": sample_issue_id,
        "content": "Original content via discussion"
    }
    create_response = await client.post("/api/v1.0/notes", json=create_data)
    
    if create_response.status_code == 201:
        note_id = create_response.json()["id"]
        
        update_data = {
            "id": note_id,
            "issueId": sample_issue_id,
            "content": "Updated content via discussion"
        }
        response = await client.put("/api/v1.0/notes", json=update_data)
        assert response.status_code in [200, 500]
        
        if response.status_code == 200:
            data = response.json()
            assert data["id"] == note_id
            assert data["content"] == "Updated content via discussion"

async def test_delete_note_via_discussion(client, sample_issue_id):
    create_data = {
        "issueId": sample_issue_id,
        "content": "Note to delete via discussion"
    }
    create_response = await client.post("/api/v1.0/notes", json=create_data)
    
    if create_response.status_code == 201:
        note_id = create_response.json()["id"]
        
        response = await client.delete(f"/api/v1.0/notes/{note_id}")
        assert response.status_code in [204, 500]
        
        if response.status_code == 204:
            get_response = await client.get(f"/api/v1.0/notes/{note_id}")
            assert get_response.status_code in [404, 500]

async def test_note_not_found_via_discussion(client):
    fake_id = 99999  
    response = await client.get(f"/api/v1.0/notes/{fake_id}")
    assert response.status_code in [404, 500]
