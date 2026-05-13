import pytest
import requests
import json

BASE_URL = "http://localhost:24110/api/v1.0"


class TestStoryCRUD:
    """Тесты для CRUD операций Story"""

    def test_10_create_story(self):
        """Create Story (Create+Delete)"""
        # Создаем writer для связи
        writer_data = {
            "login": "create.test@email.com",
            "password": "password123",
            "firstname": "Create",
            "lastname": "Test"
        }
        writer_response = requests.post(f"{BASE_URL}/writers", json=writer_data)
        assert writer_response.status_code == 201
        writer = writer_response.json()

        # Создаем story
        story_data = {
            "writer_id": writer["id"],
            "title": "Create Test Story",
            "content": "This is a story for create test."
        }
        response = requests.post(f"{BASE_URL}/stories", json=story_data)
        assert response.status_code == 201
        story = response.json()

        # Проверяем поля ответа
        assert "id" in story
        assert story["title"] == "Create Test Story"
        assert story["content"] == "This is a story for create test."
        assert story["writer_id"] == writer["id"]
        assert "created" in story
        assert "modified" in story

        # Очистка - удаляем созданные записи
        requests.delete(f"{BASE_URL}/stories/{story['id']}")
        requests.delete(f"{BASE_URL}/writers/{writer['id']}")

    def test_11_read_story(self):
        """Read Story (Create+Get+Delete)"""
        # Создаем writer
        writer_data = {
            "login": "read.test@email.com",
            "password": "password123",
            "firstname": "Read",
            "lastname": "Test"
        }
        writer_response = requests.post(f"{BASE_URL}/writers", json=writer_data)
        assert writer_response.status_code == 201
        writer = writer_response.json()

        # Создаем story
        story_data = {
            "writer_id": writer["id"],
            "title": "Read Test Story",
            "content": "This story will be read in test."
        }
        create_response = requests.post(f"{BASE_URL}/stories", json=story_data)
        assert create_response.status_code == 201
        created_story = create_response.json()
        story_id = created_story["id"]

        # Получаем story по ID
        get_response = requests.get(f"{BASE_URL}/stories/{story_id}")
        assert get_response.status_code == 200
        story = get_response.json()

        # Проверяем данные
        assert story["id"] == story_id
        assert story["title"] == "Read Test Story"
        assert story["content"] == "This story will be read in test."
        assert story["writer_id"] == writer["id"]

        # Очистка
        requests.delete(f"{BASE_URL}/stories/{story_id}")
        requests.delete(f"{BASE_URL}/writers/{writer['id']}")

    def test_12_read_stories(self):
        """Read Stories (Create+GetAll+Delete)"""
        # Создаем writer
        writer_data = {
            "login": "getall.test@email.com",
            "password": "password123",
            "firstname": "GetAll",
            "lastname": "Test"
        }
        writer_response = requests.post(f"{BASE_URL}/writers", json=writer_data)
        assert writer_response.status_code == 201
        writer = writer_response.json()

        # Создаем несколько stories
        story_ids = []
        for i in range(3):
            story_data = {
                "writer_id": writer["id"],
                "title": f"Story {i + 1} for GetAll",
                "content": f"Content of story {i + 1} for testing get all."
            }
            response = requests.post(f"{BASE_URL}/stories", json=story_data)
            assert response.status_code == 201
            story_ids.append(response.json()["id"])

        # Получаем все stories
        get_response = requests.get(f"{BASE_URL}/stories")
        assert get_response.status_code == 200
        stories = get_response.json()

        # Проверяем, что все созданные stories есть в списке
        retrieved_ids = [s["id"] for s in stories]
        for story_id in story_ids:
            assert story_id in retrieved_ids

        # Проверяем, что в списке есть наши stories
        our_stories = [s for s in stories if s["writer_id"] == writer["id"]]
        assert len(our_stories) >= 3

        # Очистка
        for story_id in story_ids:
            requests.delete(f"{BASE_URL}/stories/{story_id}")
        requests.delete(f"{BASE_URL}/writers/{writer['id']}")

    def test_13_update_story(self):
        """Update Story (Create+Update+Delete)"""
        # Создаем writer
        writer_data = {
            "login": "update.test@email.com",
            "password": "password123",
            "firstname": "Update",
            "lastname": "Test"
        }
        writer_response = requests.post(f"{BASE_URL}/writers", json=writer_data)
        assert writer_response.status_code == 201
        writer = writer_response.json()

        # Создаем story
        story_data = {
            "writer_id": writer["id"],
            "title": "Original Title",
            "content": "Original content before update."
        }
        create_response = requests.post(f"{BASE_URL}/stories", json=story_data)
        assert create_response.status_code == 201
        story = create_response.json()
        story_id = story["id"]

        # Обновляем story
        update_data = {
            "writer_id": writer["id"],
            "title": "Updated Title",
            "content": "This content has been updated successfully."
        }
        update_response = requests.put(f"{BASE_URL}/stories/{story_id}", json=update_data)
        assert update_response.status_code == 200
        updated_story = update_response.json()

        # Проверяем обновленные данные
        assert updated_story["id"] == story_id
        assert updated_story["title"] == "Updated Title"
        assert updated_story["content"] == "This content has been updated successfully."
        assert updated_story["modified"] != story["modified"]  # modified должен измениться

        # Очистка
        requests.delete(f"{BASE_URL}/stories/{story_id}")
        requests.delete(f"{BASE_URL}/writers/{writer['id']}")

    def test_14_delete_story(self):
        """Delete Story (Create+Delete+Error Delete)"""
        # Создаем writer
        writer_data = {
            "login": "delete.test@email.com",
            "password": "password123",
            "firstname": "Delete",
            "lastname": "Test"
        }
        writer_response = requests.post(f"{BASE_URL}/writers", json=writer_data)
        assert writer_response.status_code == 201
        writer = writer_response.json()

        # Создаем story
        story_data = {
            "writer_id": writer["id"],
            "title": "Story to Delete",
            "content": "This story will be deleted."
        }
        create_response = requests.post(f"{BASE_URL}/stories", json=story_data)
        assert create_response.status_code == 201
        story = create_response.json()
        story_id = story["id"]

        # Удаляем story
        delete_response = requests.delete(f"{BASE_URL}/stories/{story_id}")
        assert delete_response.status_code == 204

        # Проверяем, что story удалена (должен вернуться 404)
        get_response = requests.get(f"{BASE_URL}/stories/{story_id}")
        assert get_response.status_code == 404

        # Пробуем удалить еще раз (должна быть ошибка)
        second_delete_response = requests.delete(f"{BASE_URL}/stories/{story_id}")
        assert second_delete_response.status_code == 404

        # Очистка writer
        requests.delete(f"{BASE_URL}/writers/{writer['id']}")

    def test_15_error_update_story(self):
        """Error Update Story (Create+Update+Delete)"""
        # Создаем writer
        writer_data = {
            "login": "error.update@email.com",
            "password": "password123",
            "firstname": "Error",
            "lastname": "Update"
        }
        writer_response = requests.post(f"{BASE_URL}/writers", json=writer_data)
        assert writer_response.status_code == 201
        writer = writer_response.json()

        # Создаем story
        story_data = {
            "writer_id": writer["id"],
            "title": "Valid Title",
            "content": "Valid content for story."
        }
        create_response = requests.post(f"{BASE_URL}/stories", json=story_data)
        assert create_response.status_code == 201
        story = create_response.json()
        story_id = story["id"]

        # Попытка обновить с невалидными данными (короткий title)
        invalid_update = {
            "writer_id": writer["id"],
            "title": "A",  # слишком короткий
            "content": "Updated content."
        }
        error_response = requests.put(f"{BASE_URL}/stories/{story_id}", json=invalid_update)
        assert error_response.status_code == 400
        error_data = error_response.json()
        assert "errorMessage" in error_data
        assert "errorCode" in error_data

        # Проверяем, что story не изменилась
        get_response = requests.get(f"{BASE_URL}/stories/{story_id}")
        assert get_response.status_code == 200
        unchanged_story = get_response.json()
        assert unchanged_story["title"] == "Valid Title"

        # Попытка обновить несуществующую story
        non_existent_update = {
            "writer_id": writer["id"],
            "title": "Valid Title",
            "content": "Valid content."
        }
        error_response = requests.put(f"{BASE_URL}/stories/99999", json=non_existent_update)
        assert error_response.status_code == 404

        # Очистка
        requests.delete(f"{BASE_URL}/stories/{story_id}")
        requests.delete(f"{BASE_URL}/writers/{writer['id']}")


class TestStoryValidation:
    """Тесты валидации Story"""

    def test_create_story_validation_title_length(self):
        """Валидация: длина title меньше 2 символов"""
        story_data = {
            "writer_id": 1,
            "title": "A",
            "content": "Valid content for testing."
        }
        response = requests.post(f"{BASE_URL}/stories", json=story_data)
        assert response.status_code == 400

    def test_create_story_validation_content_length(self):
        """Валидация: длина content меньше 4 символов"""
        story_data = {
            "writer_id": 1,
            "title": "Valid Title",
            "content": "Sh"  # меньше 4 символов
        }
        response = requests.post(f"{BASE_URL}/stories", json=story_data)
        assert response.status_code == 400

    def test_create_story_validation_empty_title(self):
        """Валидация: пустой title"""
        story_data = {
            "writer_id": 1,
            "title": "",
            "content": "Valid content for testing."
        }
        response = requests.post(f"{BASE_URL}/stories", json=story_data)
        assert response.status_code == 400

    def test_create_story_validation_empty_content(self):
        """Валидация: пустой content"""
        story_data = {
            "writer_id": 1,
            "title": "Valid Title",
            "content": ""
        }
        response = requests.post(f"{BASE_URL}/stories", json=story_data)
        assert response.status_code == 400

    def test_update_non_existent_story(self):
        """Обновление несуществующей story"""
        update_data = {
            "writer_id": 1,
            "title": "Valid Title",
            "content": "Valid content."
        }
        response = requests.put(f"{BASE_URL}/stories/99999", json=update_data)
        assert response.status_code == 404

    def test_delete_non_existent_story(self):
        """Удаление несуществующей story"""
        response = requests.delete(f"{BASE_URL}/stories/99999")
        assert response.status_code == 404


class TestStoryAdditional:
    """Дополнительные тесты для Story"""

    def test_filter_stories_by_title(self):
        """Фильтрация stories по title"""
        # Создаем writer
        writer_data = {
            "login": "filter.test@email.com",
            "password": "password123",
            "firstname": "Filter",
            "lastname": "Test"
        }
        writer_response = requests.post(f"{BASE_URL}/writers", json=writer_data)
        writer = writer_response.json()

        # Создаем несколько stories с разными заголовками
        story1_data = {
            "writer_id": writer["id"],
            "title": "Python Programming",
            "content": "Content about Python programming language."
        }
        story1_resp = requests.post(f"{BASE_URL}/stories", json=story1_data)
        story1 = story1_resp.json()

        story2_data = {
            "writer_id": writer["id"],
            "title": "Java Development",
            "content": "Content about Java development."
        }
        story2_resp = requests.post(f"{BASE_URL}/stories", json=story2_data)
        story2 = story2_resp.json()

        # Фильтруем по title
        filter_response = requests.get(f"{BASE_URL}/stories?title=Python")
        assert filter_response.status_code == 200
        filtered = filter_response.json()

        # Проверяем, что только Python story в результате
        titles = [s["title"] for s in filtered]
        assert "Python Programming" in titles

        # Очистка
        requests.delete(f"{BASE_URL}/stories/{story1['id']}")
        requests.delete(f"{BASE_URL}/stories/{story2['id']}")
        requests.delete(f"{BASE_URL}/writers/{writer['id']}")