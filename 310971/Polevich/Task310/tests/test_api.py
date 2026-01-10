import pytest
from fastapi.testclient import TestClient

from app.api import app

client = TestClient(app)


def test_seed_creator_present():
    response = client.get("/api/v1.0/creators")
    assert response.status_code == 200
    creators = response.json()
    assert creators
    assert creators[0]["login"] == "sirilium.fox@icloud.com"
    assert creators[0]["firstname"] == "Максим"
    assert creators[0]["lastname"] == "Полевич"


def test_full_crud_flow():
    # create creator
    creator_payload = {
        "login": "user1",
        "password": "superSecret1",
        "firstname": "John",
        "lastname": "Doe",
    }
    creator_resp = client.post("/api/v1.0/creators", json=creator_payload)
    assert creator_resp.status_code == 201
    creator_id = creator_resp.json()["id"]

    # create tag
    tag_resp = client.post("/api/v1.0/tags", json={"name": "tech"})
    assert tag_resp.status_code == 201
    tag_id = tag_resp.json()["id"]

    # create article
    article_payload = {
        "creatorId": creator_id,
        "title": "First",
        "content": "This is a test article",
        "tagIds": [tag_id],
    }
    article_resp = client.post("/api/v1.0/articles", json=article_payload)
    assert article_resp.status_code == 201
    article_id = article_resp.json()["id"]

    # read article
    get_resp = client.get(f"/api/v1.0/articles/{article_id}")
    assert get_resp.status_code == 200
    assert get_resp.json()["creatorId"] == creator_id

    # update article
    update_payload = {**article_payload, "title": "Updated"}
    update_resp = client.put(f"/api/v1.0/articles/{article_id}", json=update_payload)
    assert update_resp.status_code == 200
    assert update_resp.json()["title"] == "Updated"

    # search article by tag id
    search_resp = client.get(f"/api/v1.0/articles/search", params={"tagIds": tag_id})
    assert search_resp.status_code == 200
    assert any(item["id"] == article_id for item in search_resp.json())

    # tag by article
    tags_resp = client.get(f"/api/v1.0/articles/{article_id}/tags")
    assert tags_resp.status_code == 200
    assert tags_resp.json()[0]["id"] == tag_id

    # messages flow
    msg_payload = {"articleId": article_id, "content": "Nice read"}
    msg_resp = client.post("/api/v1.0/messages", json=msg_payload)
    assert msg_resp.status_code == 201
    msg_id = msg_resp.json()["id"]

    msgs_resp = client.get(f"/api/v1.0/articles/{article_id}/messages")
    assert msgs_resp.status_code == 200
    assert any(m["id"] == msg_id for m in msgs_resp.json())

    # delete resources
    del_msg = client.delete(f"/api/v1.0/messages/{msg_id}")
    assert del_msg.status_code == 204
    del_art = client.delete(f"/api/v1.0/articles/{article_id}")
    assert del_art.status_code == 204
    del_tag = client.delete(f"/api/v1.0/tags/{tag_id}")
    assert del_tag.status_code == 204
    del_creator = client.delete(f"/api/v1.0/creators/{creator_id}")
    assert del_creator.status_code == 204


def test_validation_error():
    bad_creator = {
        "login": "a",
        "password": "short",
        "firstname": "",
        "lastname": "",
    }
    resp = client.post("/api/v1.0/creators", json=bad_creator)
    assert resp.status_code == 400
    body = resp.json()
    assert "errorMessage" in body
    assert "errorCode" in body
