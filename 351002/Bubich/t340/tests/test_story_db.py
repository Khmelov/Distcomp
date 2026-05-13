import pytest
import requests

BASE_URL = "http://localhost:24110/api/v1.0"


class TestStoryWithDB:
    """Тесты Story с PostgreSQL"""

    def test_create_story_with_db(self):
        """Создание story с сохранением в БД"""
        # Создаем writer
        writer_data = {
            "login": "test.db@email.com",
            "password": "password123",
            "firstname": "Test",
            "lastname": "DB"
        }
        writer_resp = requests.post(f"{BASE_URL}/writers", json=writer_data)
        assert writer_resp.status_code == 201
        writer = writer_resp.json()

        # Создаем story
        story_data = {
            "writerId": writer["id"],
            "title": "DB Test Story",
            "content": "Testing database storage."
        }
        response = requests.post(f"{BASE_URL}/stories", json=story_data)
        assert response.status_code == 201
        story = response.json()
        assert story["writerId"] == writer["id"]
        assert "created" in story
        assert "modified" in story

        # Очистка
        requests.delete(f"{BASE_URL}/stories/{story['id']}")
        requests.delete(f"{BASE_URL}/writers/{writer['id']}")

    def test_get_stories_with_pagination(self):
        """Получение stories с пагинацией"""
        response = requests.get(f"{BASE_URL}/stories?page=0&size=5&sortBy=id&sortDir=asc")
        assert response.status_code == 200
        data = response.json()
        assert "items" in data
        assert "total" in data
        assert "page" in data
        assert "size" in data
        assert isinstance(data["items"], list)

    def test_filter_stories_by_title(self):
        """Фильтрация stories по title"""
        response = requests.get(f"{BASE_URL}/stories?title=test")
        assert response.status_code == 200
        data = response.json()
        for story in data.get("items", data):
            assert "test" in story.get("title", "").lower()

    def test_first_writer_login(self):
        """Проверка первой записи Writer (login: bubichviktor@gmail.com)"""
        response = requests.get(f"{BASE_URL}/writers/1")
        assert response.status_code == 200
        writer = response.json()
        assert writer["login"] == "bubichviktor@gmail.com"
        assert writer["firstname"] == "Виктор"
        assert writer["lastname"] == "Бубич"