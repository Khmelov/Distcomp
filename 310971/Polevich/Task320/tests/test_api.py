import os

import pytest
from fastapi.testclient import TestClient

os.environ["DATABASE_URL"] = "postgresql+psycopg2://postgres:postgres@localhost:5432/distcomp"

from app.api import app  # noqa: E402

client = TestClient(app)


@pytest.fixture(autouse=True)
def clear_db():
    # For demo purposes; assumes test DB is disposable.
    yield


def test_creator_crud_and_double_delete():
    payload = {
        "login": "creator9817",
        "password": "asdfghj5699",
        "firstname": "firstname2569",
        "lastname": "lastname3851",
    }
    create_resp = client.post("/api/v1.0/creators", json=payload)
    assert create_resp.status_code == 201
    cid = create_resp.json()["id"]

    del_resp = client.delete(f"/api/v1.0/creators/{cid}")
    assert del_resp.status_code == 204

    del_resp2 = client.delete(f"/api/v1.0/creators/{cid}")
    assert del_resp2.status_code == 404


def test_article_tag_message_double_delete_flow():
    # creator
    c_resp = client.post(
        "/api/v1.0/creators",
        json={"login": "user1", "password": "Password123", "firstname": "John", "lastname": "Doe"},
    )
    assert c_resp.status_code == 201
    cid = c_resp.json()["id"]

    # tag
    t_resp = client.post("/api/v1.0/tags", json={"name": "tagname"})
    assert t_resp.status_code == 201
    tid = t_resp.json()["id"]

    # article
    a_resp = client.post(
        "/api/v1.0/articles",
        json={"creatorId": cid, "title": "title6352", "content": "content8890", "tagIds": [tid]},
    )
    assert a_resp.status_code == 201
    aid = a_resp.json()["id"]

    # message
    m_resp = client.post("/api/v1.0/messages", json={"articleId": aid, "content": "content7665"})
    assert m_resp.status_code == 201
    mid = m_resp.json()["id"]

    # delete message twice
    d1 = client.delete(f"/api/v1.0/messages/{mid}")
    assert d1.status_code == 204
    d2 = client.delete(f"/api/v1.0/messages/{mid}")
    assert d2.status_code == 404

    # delete article twice
    d1a = client.delete(f"/api/v1.0/articles/{aid}")
    assert d1a.status_code == 204
    d2a = client.delete(f"/api/v1.0/articles/{aid}")
    assert d2a.status_code == 404

    # delete tag twice
    d1t = client.delete(f"/api/v1.0/tags/{tid}")
    assert d1t.status_code == 204
    d2t = client.delete(f"/api/v1.0/tags/{tid}")
    assert d2t.status_code == 404
