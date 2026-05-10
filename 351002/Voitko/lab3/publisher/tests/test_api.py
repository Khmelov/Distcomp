def test_writer_crud(client):
    r = client.post(
        "/api/v1.0/writers",
        json={
            "login": "alice",
            "password": "secret123",
            "firstname": "Al",
            "lastname": "Ice",
        },
    )
    assert r.status_code == 201
    body = r.json()
    assert body["login"] == "alice"
    wid = body["id"]

    r = client.get(f"/api/v1.0/writers/{wid}")
    assert r.status_code == 200

    r = client.get("/api/v1.0/writers")
    assert r.status_code == 200
    assert len(r.json()) == 1

    r = client.put(
        f"/api/v1.0/writers/{wid}",
        json={
            "login": "alice2",
            "password": "secret1234",
            "firstname": "Al",
            "lastname": "Ice",
        },
    )
    assert r.status_code == 200
    assert r.json()["login"] == "alice2"

    r = client.delete("/api/v1.0/writers/999")
    assert r.status_code == 404
    err = r.json()
    assert "errorCode" in err
    assert err["errorCode"] == 40401

    r = client.delete(f"/api/v1.0/writers/{wid}")
    assert r.status_code == 204


def test_news_note_flow_and_subresources(client):
    wr = client.post(
        "/api/v1.0/writers",
        json={
            "login": "w1",
            "password": "password12",
            "firstname": "Fn",
            "lastname": "Ln",
        },
    ).json()
    wid = wr["id"]

    news_r = client.post(
        "/api/v1.0/news",
        json={"writerId": wid, "title": "Hello", "content": "World here"},
    )
    assert news_r.status_code == 201
    nid = news_r.json()["id"]

    r = client.get(f"/api/v1.0/news/{nid}/writer")
    assert r.status_code == 200
    assert r.json()["login"] == "w1"

    lab = client.post("/api/v1.0/labels", json={"name": "tag1"}).json()
    # связь many-to-many через ORM в тесте хранилища; для API добавим attach отдельно при необходимости
    assert "id" in lab

    note_r = client.post(
        "/api/v1.0/notes",
        json={"newsId": nid, "content": "comment"},
    )
    assert note_r.status_code == 201

    r = client.get(f"/api/v1.0/news/{nid}/notes")
    assert r.status_code == 200
    assert len(r.json()) == 1

    r = client.get(f"/api/v1.0/news/{nid}/labels")
    assert r.status_code == 200
    assert r.json() == []

    r = client.get("/api/v1.0/news/search", params={"writer_login": "w1"})
    assert r.status_code == 200
    assert len(r.json()) == 1
    assert r.json()[0]["id"] == nid


def test_news_create_with_label_names_persists_labels(client):
    wr = client.post(
        "/api/v1.0/writers",
        json={
            "login": "lw",
            "password": "password12",
            "firstname": "aa",
            "lastname": "bb",
        },
    ).json()
    wid = wr["id"]

    r = client.post(
        "/api/v1.0/news",
        json={
            "writerId": wid,
            "title": "Title distinct xyz",
            "content": "Body " * 2,
            "labelNames": ["red22", "green22", "blue22"],
        },
    )
    assert r.status_code == 201
    nid = r.json()["id"]

    r = client.get(f"/api/v1.0/news/{nid}/labels")
    assert r.status_code == 200
    names = sorted(x["name"] for x in r.json())
    assert names == ["blue22", "green22", "red22"]

    r = client.post(
        "/api/v1.0/news",
        json={
            "writerId": wid,
            "title": "Title alias labels",
            "content": "Body2 " * 2,
            "labels": ["a1", "b2"],
        },
    )
    assert r.status_code == 201


def test_delete_news_removes_orphan_labels(client):
    wr = client.post(
        "/api/v1.0/writers",
        json={
            "login": "lw2",
            "password": "password12",
            "firstname": "aa",
            "lastname": "bb",
        },
    ).json()
    wid = wr["id"]

    r = client.post(
        "/api/v1.0/news",
        json={
            "writerId": wid,
            "title": "News orphan labels",
            "content": "Body " * 2,
            "labelNames": ["red41", "green41", "blue41"],
        },
    )
    assert r.status_code == 201
    nid = r.json()["id"]

    names = {x["name"] for x in client.get("/api/v1.0/labels").json()}
    assert {"red41", "green41", "blue41"}.issubset(names)

    assert client.delete(f"/api/v1.0/news/{nid}").status_code == 204

    names_after = {x["name"] for x in client.get("/api/v1.0/labels").json()}
    assert not names_after & {"red41", "green41", "blue41"}


def test_delete_news_keeps_label_used_by_other_news(client):
    wr = client.post(
        "/api/v1.0/writers",
        json={
            "login": "lw3",
            "password": "password12",
            "firstname": "aa",
            "lastname": "bb",
        },
    ).json()
    wid = wr["id"]

    r1 = client.post(
        "/api/v1.0/news",
        json={
            "writerId": wid,
            "title": "N1 shared lbl",
            "content": "Body " * 2,
            "labelNames": ["shared77"],
        },
    )
    r2 = client.post(
        "/api/v1.0/news",
        json={
            "writerId": wid,
            "title": "N2 shared lbl",
            "content": "Body2 " * 2,
            "labelNames": ["shared77"],
        },
    )
    assert r1.status_code == 201 and r2.status_code == 201
    n1, n2 = r1.json()["id"], r2.json()["id"]

    assert client.delete(f"/api/v1.0/news/{n1}").status_code == 204
    names = {x["name"] for x in client.get("/api/v1.0/labels").json()}
    assert "shared77" in names

    assert client.delete(f"/api/v1.0/news/{n2}").status_code == 204
    names = {x["name"] for x in client.get("/api/v1.0/labels").json()}
    assert "shared77" not in names


def test_validation_error_format(client):
    r = client.post(
        "/api/v1.0/writers",
        json={
            "login": "x",
            "password": "short",
            "firstname": "a",
            "lastname": "b",
        },
    )
    assert r.status_code == 400
    data = r.json()
    assert data.get("errorCode") == 40001
