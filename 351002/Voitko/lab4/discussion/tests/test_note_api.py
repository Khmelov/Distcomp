def test_create_list_get_update_delete(discussion_client):
    c = discussion_client
    r = c.post("/api/v1.0/notes", json={"newsId": 10, "content": "hello note"})
    assert r.status_code == 201
    body = r.json()
    assert body["newsId"] == 10
    assert body["content"] == "hello note"
    nid = body["id"]

    r = c.get("/api/v1.0/notes")
    assert r.status_code == 200
    assert len(r.json()) == 1

    r = c.get(f"/api/v1.0/notes/{nid}")
    assert r.status_code == 200
    assert r.json()["content"] == "hello note"

    r = c.get("/api/v1.0/notes/by-news/10")
    assert r.status_code == 200
    assert len(r.json()) == 1

    r = c.put(
        f"/api/v1.0/notes/{nid}",
        json={"newsId": 11, "content": "updated"},
    )
    assert r.status_code == 200
    assert r.json()["newsId"] == 11

    r = c.get("/api/v1.0/notes/by-news/10")
    assert r.status_code == 200
    assert r.json() == []

    r = c.get("/api/v1.0/notes/by-news/11")
    assert r.status_code == 200
    assert len(r.json()) == 1

    r = c.delete(f"/api/v1.0/notes/{nid}")
    assert r.status_code == 204

    r = c.get(f"/api/v1.0/notes/{nid}")
    assert r.status_code == 404
    err = r.json()
    assert err.get("errorCode") == 40404


def test_delete_by_news(discussion_client):
    c = discussion_client
    for i in range(3):
        r = c.post("/api/v1.0/notes", json={"newsId": 5, "content": f"c{i}"})
        assert r.status_code == 201
    assert len(c.get("/api/v1.0/notes/by-news/5").json()) == 3

    r = c.delete("/api/v1.0/notes/by-news/5")
    assert r.status_code == 204
    assert c.get("/api/v1.0/notes/by-news/5").json() == []
    assert c.get("/api/v1.0/notes").json() == []


def test_note_not_found(discussion_client):
    r = discussion_client.get("/api/v1.0/notes/999999999999")
    assert r.status_code == 404
    assert r.json().get("errorCode") == 40404
