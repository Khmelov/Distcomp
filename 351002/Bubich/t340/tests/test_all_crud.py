import requests
import pytest

BASE_URL = "http://localhost:24110/api/v1.0"


class TestWriterCRUD:
    """Полный набор тестов для Writer"""

    def test_01_create_writer(self):
        """Создание writer"""
        writer_data = {
            "login": "test_writer_crud@email.com",
            "password": "securepassword123",
            "firstname": "Test",
            "lastname": "Writer"
        }
        response = requests.post(f"{BASE_URL}/writers", json=writer_data)
        assert response.status_code == 201
        data = response.json()
        assert data["login"] == "test_writer_crud@email.com"
        assert data["firstname"] == "Test"
        assert "id" in data
        pytest.writer_id = data["id"]

    def test_02_get_writer_by_id(self):
        """Получение writer по ID"""
        response = requests.get(f"{BASE_URL}/writers/{pytest.writer_id}")
        assert response.status_code == 200
        data = response.json()
        assert data["id"] == pytest.writer_id

    def test_03_get_all_writers(self):
        """Получение всех writers"""
        response = requests.get(f"{BASE_URL}/writers")
        assert response.status_code == 200
        data = response.json()
        assert isinstance(data, list)
        assert len(data) > 0

    def test_04_update_writer(self):
        """Обновление writer"""
        update_data = {
            "login": "updated_writer@email.com",
            "password": "newpassword456",
            "firstname": "Updated",
            "lastname": "Name"
        }
        response = requests.put(f"{BASE_URL}/writers/{pytest.writer_id}", json=update_data)
        assert response.status_code == 200
        data = response.json()
        assert data["firstname"] == "Updated"
        assert data["lastname"] == "Name"

    def test_05_delete_writer(self):
        """Удаление writer"""
        # Создаем нового writer для удаления
        create_resp = requests.post(f"{BASE_URL}/writers", json={
            "login": "delete_me@email.com",
            "password": "password123",
            "firstname": "Delete",
            "lastname": "Me"
        })
        writer_id = create_resp.json()["id"]

        # Удаляем
        delete_resp = requests.delete(f"{BASE_URL}/writers/{writer_id}")
        assert delete_resp.status_code == 204

        # Проверяем что удален
        get_resp = requests.get(f"{BASE_URL}/writers/{writer_id}")
        assert get_resp.status_code == 404

    def test_06_validation_error(self):
        """Проверка валидации - короткий пароль"""
        invalid_data = {
            "login": "test",
            "password": "123",  # слишком короткий
            "firstname": "Test",
            "lastname": "User"
        }
        response = requests.post(f"{BASE_URL}/writers", json=invalid_data)
        assert response.status_code == 400
        data = response.json()
        assert "errorMessage" in data
        assert "errorCode" in data

    def test_07_not_found_error(self):
        """Проверка 404 ошибки"""
        response = requests.get(f"{BASE_URL}/writers/99999")
        assert response.status_code == 404


class TestStoryCRUD:
    """Полный набор тестов для Story"""

    def test_01_create_story(self):
        """Создание story"""
        # Сначала создаем writer
        writer_data = {
            "login": "story_test_writer@email.com",
            "password": "password123",
            "firstname": "Story",
            "lastname": "Test"
        }
        writer_resp = requests.post(f"{BASE_URL}/writers", json=writer_data)
        writer_id = writer_resp.json()["id"]

        # Создаем story
        story_data = {
            "writerId": writer_id,
            "title": "Test Story Title",
            "content": "This is a test story content for CRUD testing."
        }
        response = requests.post(f"{BASE_URL}/stories", json=story_data)
        assert response.status_code == 201
        data = response.json()
        assert data["title"] == "Test Story Title"
        pytest.story_id = data["id"]
        pytest.writer_id_for_story = writer_id

    def test_02_get_story_by_id(self):
        """Получение story по ID"""
        response = requests.get(f"{BASE_URL}/stories/{pytest.story_id}")
        assert response.status_code == 200
        data = response.json()
        assert data["id"] == pytest.story_id

    def test_03_get_all_stories(self):
        """Получение всех stories"""
        response = requests.get(f"{BASE_URL}/stories")
        assert response.status_code == 200
        data = response.json()
        assert "items" in data or isinstance(data, list)

    def test_04_update_story(self):
        """Обновление story"""
        update_data = {
            "writerId": pytest.writer_id_for_story,
            "title": "Updated Story Title",
            "content": "This content has been updated."
        }
        response = requests.put(f"{BASE_URL}/stories/{pytest.story_id}", json=update_data)
        assert response.status_code == 200
        data = response.json()
        assert data["title"] == "Updated Story Title"

    def test_05_delete_story(self):
        """Удаление story"""
        delete_resp = requests.delete(f"{BASE_URL}/stories/{pytest.story_id}")
        assert delete_resp.status_code == 204

        # Проверяем что удалена
        get_resp = requests.get(f"{BASE_URL}/stories/{pytest.story_id}")
        assert get_resp.status_code == 404


class TestMarkCRUD:
    """Полный набор тестов для Mark"""

    def test_01_create_mark(self):
        """Создание mark"""
        mark_data = {"name": "test_mark_crud"}
        response = requests.post(f"{BASE_URL}/marks", json=mark_data)
        assert response.status_code == 201
        data = response.json()
        assert data["name"] == "test_mark_crud"
        pytest.mark_id = data["id"]

    def test_02_get_mark(self):
        """Получение mark"""
        response = requests.get(f"{BASE_URL}/marks/{pytest.mark_id}")
        assert response.status_code == 200

    def test_03_update_mark(self):
        """Обновление mark"""
        response = requests.put(f"{BASE_URL}/marks/{pytest.mark_id}", json={"name": "updated_mark"})
        assert response.status_code == 200
        assert response.json()["name"] == "updated_mark"

    def test_04_delete_mark(self):
        """Удаление mark"""
        response = requests.delete(f"{BASE_URL}/marks/{pytest.mark_id}")
        assert response.status_code == 204


class TestCommentCRUD:
    """Полный набор тестов для Comment"""

    def test_01_create_comment(self):
        """Создание comment"""
        # Создаем writer и story
        writer_resp = requests.post(f"{BASE_URL}/writers", json={
            "login": "comment_test@email.com",
            "password": "password123",
            "firstname": "Comment",
            "lastname": "Test"
        })
        writer_id = writer_resp.json()["id"]

        story_resp = requests.post(f"{BASE_URL}/stories", json={
            "writerId": writer_id,
            "title": "Story for Comments",
            "content": "This story will have comments."
        })
        story_id = story_resp.json()["id"]

        # Создаем comment
        comment_data = {
            "storyId": story_id,
            "content": "This is a test comment."
        }
        response = requests.post(f"{BASE_URL}/comments", json=comment_data)
        assert response.status_code == 201
        data = response.json()
        assert data["content"] == "This is a test comment."

        pytest.comment_id = data["id"]
        pytest.story_id_for_comment = story_id
        pytest.writer_id_for_comment = writer_id

    def test_02_get_comment(self):
        """Получение comment"""
        response = requests.get(f"{BASE_URL}/comments/{pytest.comment_id}")
        assert response.status_code == 200

    def test_03_update_comment(self):
        """Обновление comment"""
        response = requests.put(f"{BASE_URL}/comments/{pytest.comment_id}", json={
            "storyId": pytest.story_id_for_comment,
            "content": "Updated comment content."
        })
        assert response.status_code == 200
        assert response.json()["content"] == "Updated comment content."

    def test_04_delete_comment(self):
        """Удаление comment"""
        response = requests.delete(f"{BASE_URL}/comments/{pytest.comment_id}")
        assert response.status_code == 204