import pytest

@pytest.mark.asyncio
async def test_label_full_crud(client):
    # STEP 1. CREATE LABEL
    payload = {"name": "Work"}
    response = await client.post("/api/v1.0/labels", json=payload)
    assert response.status_code == 201
    label_id = response.json()["id"]

    # STEP 2. GET ALL LABELS
    response = await client.get("/api/v1.0/labels")
    assert response.status_code == 200
    assert len(response.json()) >= 1

    # STEP 3. UPDATE LABEL
    response = await client.put(f"/api/v1.0/labels/{label_id}", json={"name": "Home"})
    assert response.status_code == 200
    assert response.json()["name"] == "Home"

    # STEP 4. DELETE LABEL
    response = await client.delete(f"/api/v1.0/labels/{label_id}")
    assert response.status_code == 204

    # STEP 5. ERROR CASE: LABEL NOT FOUND (CODE 40409)
    response = await client.get(f"/api/v1.0/labels/{label_id}")
    assert response.status_code == 404
    assert response.json()["errorCode"] == "40409"