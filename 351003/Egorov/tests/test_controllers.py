from fastapi.testclient import TestClient

from main import get_app


client = TestClient(get_app())


def test_creator_crud():
    response = client.post(
        "/api/v1.0/creators",
        json={"login": "user1", "name": "User One", "email": "user1@example.com"},
    )
    assert response.status_code == 201
    creator = response.json()
    creator_id = creator["id"]

    response = client.get(f"/api/v1.0/creators/{creator_id}")
    assert response.status_code == 200
    assert response.json()["login"] == "user1"

    response = client.put(
        f"/api/v1.0/creators/{creator_id}",
        json={"login": "user1upd", "name": "User One Updated", "email": "user1upd@example.com"},
    )
    assert response.status_code == 200
    assert response.json()["login"] == "user1upd"

    response = client.get("/api/v1.0/creators")
    assert response.status_code == 200
    assert len(response.json()) >= 1

    response = client.delete(f"/api/v1.0/creators/{creator_id}")
    assert response.status_code == 204

    response = client.get(f"/api/v1.0/creators/{creator_id}")
    assert response.status_code == 404


def test_marker_and_story_relations():
    creator = client.post(
        "/api/v1.0/creators",
        json={"login": "storyuser", "name": "Story User", "email": "story@example.com"},
    ).json()

    marker1 = client.post("/api/v1.0/markers", json={"name": "fantasy"}).json()
    marker2 = client.post("/api/v1.0/markers", json={"name": "adventure"}).json()

    story_resp = client.post(
        "/api/v1.0/stories",
        json={
            "title": "Epic Story",
            "content": "Once upon a time",
            "creator_id": creator["id"],
            "marker_ids": [marker1["id"], marker2["id"]],
        },
    )
    assert story_resp.status_code == 201
    story = story_resp.json()

    resp = client.get(f"/api/v1.0/story/{story['id']}/creator")
    assert resp.status_code == 200
    assert resp.json()["id"] == creator["id"]

    resp = client.get(f"/api/v1.0/story/{story['id']}/markers")
    assert resp.status_code == 200
    markers = resp.json()
    assert {m["id"] for m in markers} == {marker1["id"], marker2["id"]}


def test_notice_crud_and_story_relation():
    creator = client.post(
        "/api/v1.0/creators",
        json={"login": "noticeuser", "name": "Notice User", "email": "notice@example.com"},
    ).json()
    story = client.post(
        "/api/v1.0/stories",
        json={
            "title": "Story With Notices",
            "content": "Content",
            "creator_id": creator["id"],
            "marker_ids": [],
        },
    ).json()

    resp = client.post(
        "/api/v1.0/notices",
        json={"content": "First notice", "story_id": story["id"]},
    )
    assert resp.status_code == 201
    notice = resp.json()

    resp = client.get(f"/api/v1.0/notices/{notice['id']}")
    assert resp.status_code == 200

    resp = client.put(
        f"/api/v1.0/notices/{notice['id']}",
        json={"content": "Updated notice", "story_id": story["id"]},
    )
    assert resp.status_code == 200
    assert resp.json()["content"] == "Updated notice"

    resp = client.get(f"/api/v1.0/story/{story['id']}/notices")
    assert resp.status_code == 200
    assert len(resp.json()) >= 1

    resp = client.delete(f"/api/v1.0/notices/{notice['id']}")
    assert resp.status_code == 204


def test_validation_error_format():
    resp = client.post(
        "/api/v1.0/creators",
        json={"name": "Bad User", "email": "bad@example.com"},
    )
    assert resp.status_code == 400

