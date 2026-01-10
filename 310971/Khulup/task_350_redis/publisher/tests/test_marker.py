import pytest
import pytest_asyncio

pytestmark = pytest.mark.asyncio

@pytest_asyncio.fixture
async def created_marker(client):
    resp = await client.post("/api/v1.0/markers", json={"name": "InitialMarker"})
    return resp.json()

async def test_create_marker_success(client):
    response = await client.post("/api/v1.0/markers", json={"name": "TestMarker"})
    assert response.status_code == 201
    data = response.json()
    assert "id" in data
    assert data["name"] == "TestMarker"

async def test_create_marker_validation_error(client):
    resp_short = await client.post("/api/v1.0/markers", json={"name": "A"})
    assert resp_short.status_code == 422

    long_name = "X" * 33
    resp_long = await client.post("/api/v1.0/markers", json={"name": long_name})
    assert resp_long.status_code == 422

async def test_get_all_markers(client, created_marker):
    await client.post("/api/v1.0/markers", json={"name": "AnotherMarker"})
    resp = await client.get("/api/v1.0/markers")
    assert resp.status_code == 200
    lst = resp.json()
    assert isinstance(lst, list)
    assert len(lst) >= 2
    assert any(m["name"] == "AnotherMarker" for m in lst)

async def test_get_marker_by_id_success(client, created_marker):
    mid = created_marker["id"]
    resp = await client.get(f"/api/v1.0/markers/{mid}")
    assert resp.status_code == 200
    data = resp.json()
    assert data["id"] == mid
    assert data["name"] == created_marker["name"]

async def test_get_marker_not_found(client):
    resp = await client.get("/api/v1.0/markers/9999")
    assert resp.status_code == 404
    data = resp.json()
    assert "errorMessage" in data
    assert "errorCode" in data
    assert str(data["errorCode"]).startswith("404")

async def test_update_marker_success(client, created_marker):
    mid = created_marker["id"]
    resp = await client.put("/api/v1.0/markers", json={"id": mid, "name": "UpdatedName"})
    assert resp.status_code == 200
    data = resp.json()
    assert data["id"] == mid
    assert data["name"] == "UpdatedName"

async def test_update_marker_not_found(client):
    resp = await client.put("/api/v1.0/markers", json={"id": 9999, "name": "DoesNotExist"})
    assert resp.status_code == 404
    data = resp.json()
    assert "errorMessage" in data
    assert "errorCode" in data
    assert str(data["errorCode"]).startswith("404")

async def test_delete_marker_success(client, created_marker):
    mid = created_marker["id"]
    resp = await client.delete(f"/api/v1.0/markers/{mid}")
    assert resp.status_code == 204
    get_resp = await client.get(f"/api/v1.0/markers/{mid}")
    assert get_resp.status_code == 404

async def test_delete_marker_not_found(client):
    resp = await client.delete("/api/v1.0/markers/9999")
    assert resp.status_code == 404
    data = resp.json()
    assert "errorMessage" in data
    assert "errorCode" in data
    assert str(data["errorCode"]).startswith("404")
