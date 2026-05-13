import requests

BASE_URL = "http://localhost:24110/api/v1.0"


class TestCommentCRUD:
    """Тесты для CRUD операций Comment"""

    def test_create_comment(self):
        """Создание comment"""
        # Создаем writer и story для связи
        writer_resp = requests.post(f"{BASE_URL}/writers", json={
            "login": "comment_test@email.com",
            "password": "password123",
            "firstname": "Comment",
            "lastname": "Test"
        })
        writer = writer_resp.json()

        story_resp = requests.post(f"{BASE_URL}/stories", json={
            "writer_id": writer["id"],
            "title": "Story for Comment",
            "content": "This story has comments."
        })
        story = story_resp.json()

        # Создаем comment
        comment_data = {
            "story_id": story["id"],
            "content": "This is a test comment."
        }
        response = requests.post(f"{BASE_URL}/comments", json=comment_data)
        assert response.status_code == 201
        comment = response.json()
        assert comment["content"] == "This is a test comment."
        assert comment["story_id"] == story["id"]

        # Очистка
        requests.delete(f"{BASE_URL}/comments/{comment['id']}")
        requests.delete(f"{BASE_URL}/stories/{story['id']}")
        requests.delete(f"{BASE_URL}/writers/{writer['id']}")