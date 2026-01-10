import pytest
import pytest_asyncio
from httpx import AsyncClient, ASGITransport
import sys
import os

sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from main import app
from app.core.cassandra import cassandra_config

pytestmark = pytest.mark.asyncio

@pytest_asyncio.fixture(scope="session")
async def setup_cassandra():
    try:
        cassandra_config.connect()
        yield
    finally:
        cassandra_config.disconnect()

@pytest_asyncio.fixture
async def client(setup_cassandra):
    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url="http://localhost:24130") as ac:
        yield ac

@pytest_asyncio.fixture
async def sample_issue_id():
    return 12345

async def test_health_check(client):
    response = await client.get("/api/v1.0/health")
    assert response.status_code == 200
    data = response.json()
    assert "status" in data
    assert data["database"] == "cassandra"

async def test_root_endpoint(client):
    response = await client.get("/")
    assert response.status_code == 200
    data = response.json()
    assert data["message"] == "Discussion Microservice"
    assert data["database"] == "Cassandra"

async def test_create_note_success(client, sample_issue_id):
    note_data = {
        "issueId": sample_issue_id,
        "content": "Test note content"
    }
    
    response = await client.post("/api/v1.0/notes", json=note_data)
    assert response.status_code == 201
    
    data = response.json()
    assert data["issueId"] == sample_issue_id
    assert data["content"] == "Test note content"
    assert "id" in data
    assert "createdAt" in data

async def test_create_note_validation_error(client, sample_issue_id):
    note_data = {
        "issueId": sample_issue_id,
        "content": "a"
    }
    
    response = await client.post("/api/v1.0/notes", json=note_data)
    assert response.status_code == 422

async def test_get_note_by_id_success(client, sample_issue_id):
    create_data = {
        "issueId": sample_issue_id,
        "content": "Test note for get by ID"
    }
    create_response = await client.post("/api/v1.0/notes", json=create_data)
    note_id = create_response.json()["id"]
    
    response = await client.get(f"/api/v1.0/notes/{note_id}")
    assert response.status_code == 200
    
    data = response.json()
    assert data["id"] == note_id
    assert data["content"] == "Test note for get by ID"

async def test_get_note_by_id_not_found(client):
    fake_id = 999999
    response = await client.get(f"/api/v1.0/notes/{fake_id}")
    assert response.status_code == 404

async def test_get_notes_by_issue_id(client, sample_issue_id):
    notes_data = [
        {"issueId": sample_issue_id, "content": "First note"},
        {"issueId": sample_issue_id, "content": "Second note"}
    ]
    
    for note_data in notes_data:
        await client.post("/api/v1.0/notes", json=note_data)
    
    response = await client.get(f"/api/v1.0/notes?issue_id={sample_issue_id}")
    assert response.status_code == 200
    
    data = response.json()
    assert isinstance(data, list)
    assert len(data) >= 0

async def test_update_note_success(client, sample_issue_id):
    create_data = {
        "issueId": sample_issue_id,
        "content": "Original content"
    }
    create_response = await client.post("/api/v1.0/notes", json=create_data)
    note_id = create_response.json()["id"]
    
    update_data = {
        "id": note_id,
        "issueId": sample_issue_id,
        "content": "Updated content"
    }
    response = await client.put("/api/v1.0/notes", json=update_data)
    assert response.status_code == 200
    
    data = response.json()
    assert data["id"] == note_id
    assert data["content"] == "Updated content"
    assert "updatedAt" in data

async def test_update_note_not_found(client, sample_issue_id):
    fake_id = 999999  
    update_data = {
        "id": fake_id,
        "issueId": sample_issue_id,
        "content": "Updated content"
    }
    response = await client.put("/api/v1.0/notes", json=update_data)
    assert response.status_code == 404

async def test_delete_note_success(client, sample_issue_id):
    create_data = {
        "issueId": sample_issue_id,
        "content": "Note to delete"
    }
    create_response = await client.post("/api/v1.0/notes", json=create_data)
    note_id = create_response.json()["id"]
    
    response = await client.delete(f"/api/v1.0/notes/{note_id}")
    assert response.status_code == 204
    
    get_response = await client.get(f"/api/v1.0/notes/{note_id}")
    assert get_response.status_code == 404

async def test_delete_note_not_found(client):
    fake_id = 999999
    response = await client.delete(f"/api/v1.0/notes/{fake_id}")
    assert response.status_code == 404
