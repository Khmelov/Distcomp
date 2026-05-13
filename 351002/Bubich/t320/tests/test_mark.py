import requests

BASE_URL = "http://localhost:24110/api/v1.0"


class TestMarkCRUD:
    """Тесты для CRUD операций Mark"""

    def test_create_mark(self):
        """Создание mark"""
        mark_data = {"name": "test_mark"}
        response = requests.post(f"{BASE_URL}/marks", json=mark_data)
        assert response.status_code == 201
        mark = response.json()
        assert mark["name"] == "test_mark"

        # Очистка
        requests.delete(f"{BASE_URL}/marks/{mark['id']}")

    def test_get_mark_by_id(self):
        """Получение mark по ID"""
        create_resp = requests.post(f"{BASE_URL}/marks", json={"name": "get_test"})
        mark = create_resp.json()

        get_resp = requests.get(f"{BASE_URL}/marks/{mark['id']}")
        assert get_resp.status_code == 200
        assert get_resp.json()["name"] == "get_test"

        requests.delete(f"{BASE_URL}/marks/{mark['id']}")

    def test_get_all_marks(self):
        """Получение всех marks"""
        response = requests.get(f"{BASE_URL}/marks")
        assert response.status_code == 200
        assert isinstance(response.json(), list)

    def test_update_mark(self):
        """Обновление mark"""
        create_resp = requests.post(f"{BASE_URL}/marks", json={"name": "old_name"})
        mark = create_resp.json()

        update_resp = requests.put(f"{BASE_URL}/marks/{mark['id']}", json={"name": "new_name"})
        assert update_resp.status_code == 200
        assert update_resp.json()["name"] == "new_name"

        requests.delete(f"{BASE_URL}/marks/{mark['id']}")

    def test_delete_mark(self):
        """Удаление mark"""
        create_resp = requests.post(f"{BASE_URL}/marks", json={"name": "delete_me"})
        mark = create_resp.json()

        delete_resp = requests.delete(f"{BASE_URL}/marks/{mark['id']}")
        assert delete_resp.status_code == 204

        get_resp = requests.get(f"{BASE_URL}/marks/{mark['id']}")
        assert get_resp.status_code == 404