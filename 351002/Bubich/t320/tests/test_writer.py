import requests

BASE_URL = "http://localhost:24110/api/v1.0"


class TestWriterCRUD:
    """Тесты для CRUD операций Writer"""

    def test_create_writer(self):
        """Создание writer"""
        writer_data = {
            "login": "testuser@email.com",
            "password": "securepass123",
            "firstname": "John",
            "lastname": "Doe"
        }
        response = requests.post(f"{BASE_URL}/writers", json=writer_data)
        assert response.status_code == 201
        writer = response.json()
        assert writer["login"] == "testuser@email.com"
        assert writer["firstname"] == "John"
        assert writer["lastname"] == "Doe"
        assert "password" not in writer  # пароль не должен возвращаться

        # Очистка
        requests.delete(f"{BASE_URL}/writers/{writer['id']}")

    def test_get_writer_by_id(self):
        """Получение writer по ID"""
        # Создаем writer
        writer_data = {
            "login": "getid@email.com",
            "password": "securepass123",
            "firstname": "Get",
            "lastname": "ById"
        }
        create_resp = requests.post(f"{BASE_URL}/writers", json=writer_data)
        writer = create_resp.json()

        # Получаем по ID
        get_resp = requests.get(f"{BASE_URL}/writers/{writer['id']}")
        assert get_resp.status_code == 200
        assert get_resp.json()["id"] == writer["id"]

        # Очистка
        requests.delete(f"{BASE_URL}/writers/{writer['id']}")

    def test_get_all_writers(self):
        """Получение всех writers"""
        response = requests.get(f"{BASE_URL}/writers")
        assert response.status_code == 200
        assert isinstance(response.json(), list)

    def test_update_writer(self):
        """Обновление writer"""
        # Создаем
        writer_data = {
            "login": "update@email.com",
            "password": "securepass123",
            "firstname": "Update",
            "lastname": "Test"
        }
        create_resp = requests.post(f"{BASE_URL}/writers", json=writer_data)
        writer = create_resp.json()

        # Обновляем
        updated_data = {
            "login": "updated@email.com",
            "password": "newpassword123",
            "firstname": "Updated",
            "lastname": "Writer"
        }
        update_resp = requests.put(f"{BASE_URL}/writers/{writer['id']}", json=updated_data)
        assert update_resp.status_code == 200
        assert update_resp.json()["firstname"] == "Updated"

        # Очистка
        requests.delete(f"{BASE_URL}/writers/{writer['id']}")

    def test_delete_writer(self):
        """Удаление writer"""
        writer_data = {
            "login": "delete@email.com",
            "password": "securepass123",
            "firstname": "Delete",
            "lastname": "Me"
        }
        create_resp = requests.post(f"{BASE_URL}/writers", json=writer_data)
        writer = create_resp.json()

        # Удаляем
        delete_resp = requests.delete(f"{BASE_URL}/writers/{writer['id']}")
        assert delete_resp.status_code == 204

        # Проверяем удаление
        get_resp = requests.get(f"{BASE_URL}/writers/{writer['id']}")
        assert get_resp.status_code == 404