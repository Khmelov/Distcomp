import pytest
from rest_framework import status


@pytest.mark.django_db
class TestWriterCRUD:
    def test_create_writer(self, api_client):
        url = "/api/v1.0/writers"
        data = {
            "login": "test_writer",
            "password": "password123",
            "firstname": "Ivan",
            "lastname": "Ivanov"
        }
        response = api_client.post(url, data, format='json')
        assert response.status_code == status.HTTP_201_CREATED
        assert response.data['login'] == "test_writer"

    def test_duplicate_login_returns_403(self, api_client):
        url = "/api/v1.0/writers"
        data = {"login": "dup", "password": "password123", "firstname": "A", "lastname": "B"}
        api_client.post(url, data, format='json')
        response = api_client.post(url, data, format='json')
        assert response.status_code == status.HTTP_403_FORBIDDEN

    def test_get_writer_by_id(self, api_client):
        res = api_client.post("/api/v1.0/writers",
                              {"login": "get_id", "password": "password123", "firstname": "A", "lastname": "B"})
        writer_id = res.data['id']
        response = api_client.get(f"/api/v1.0/writers/{writer_id}")
        assert response.status_code == status.HTTP_200_OK
        assert response.data['id'] == writer_id


@pytest.mark.django_db
class TestMarkerCRUD:
    def test_marker_lifecycle(self, api_client):
        url = "/api/v1.0/markers"
        res = api_client.post(url, {"name": "urgent"}, format='json')
        marker_id = res.data['id']
        assert res.status_code == status.HTTP_201_CREATED

        res = api_client.put(f"{url}/{marker_id}", {"name": "very_urgent"}, format='json')
        assert res.data['name'] == "very_urgent"

        res = api_client.delete(f"{url}/{marker_id}")
        assert res.status_code == status.HTTP_204_NO_CONTENT


@pytest.mark.django_db
class TestStoryCRUD:
    @pytest.fixture
    def writer(self, api_client):
        res = api_client.post("/api/v1.0/writers",
                              {"login": "story_w", "password": "password123", "firstname": "A", "lastname": "B"})
        return res.data

    def test_create_story_with_markers(self, api_client, writer):
        url = "/api/v1.0/stories"
        api_client.post("/api/v1.0/markers", {"name": "red123"})

        data = {
            "writerId": writer['id'],
            "title": "My Story",
            "content": "Once upon a time...",
            "markers": ["red123"]
        }
        response = api_client.post(url, data, format='json')
        assert response.status_code == status.HTTP_201_CREATED
        assert response.data['writerId'] == writer['id']

    def test_delete_story_removes_orphaned_markers(self, api_client, writer):
        api_client.post("/api/v1.0/markers", {"name": "to_be_deleted"})
        story_res = api_client.post("/api/v1.0/stories", {
            "writerId": writer['id'],
            "title": "T", "content": "C", "markers": ["to_be_deleted"]
        }, format='json')

        story_id = story_res.data['id']

        api_client.delete(f"/api/v1.0/stories/{story_id}")

        marker_check = api_client.get("/api/v1.0/markers")
        assert not any(m['name'] == "to_be_deleted" for m in marker_check.data)


@pytest.mark.django_db
class TestNoteCRUD:
    @pytest.fixture
    def writer(self, api_client):
        res = api_client.post("/api/v1.0/writers",
                              {"login": "note_w", "password": "password123", "firstname": "A", "lastname": "B"})
        return res.data

    def test_note_lifecycle(self, api_client, writer):
        url = "/api/v1.0/notes"
        data = {"writerId": writer['id'], "content": "Secret note"}
        res = api_client.post(url, data, format='json')
        note_id = res.data['id']
        assert res.status_code == status.HTTP_201_CREATED

        res = api_client.put(f"{url}/{note_id}", {"writerId": writer['id'], "content": "Updated note"}, format='json')
        assert res.data['content'] == "Updated note"

        res = api_client.delete(f"{url}/{note_id}")
        assert res.status_code == status.HTTP_204_NO_CONTENT