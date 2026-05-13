import requests

BASE_URL = "http://localhost:24110/api/v1.0"


def test_duplicate_writer():
    """Проверка ответа 403 при создании дубликата"""

    # Создаем первого writer
    writer_data = {
        "login": "duplicate_test",
        "password": "password123",
        "firstname": "Test",
        "lastname": "Duplicate"
    }

    r1 = requests.post(f"{BASE_URL}/writers", json=writer_data)
    print(f"Первый запрос: Status {r1.status_code}")
    assert r1.status_code == 201

    # Пытаемся создать дубликат
    r2 = requests.post(f"{BASE_URL}/writers", json=writer_data)
    print(f"Дубликат: Status {r2.status_code}")
    print(f"Response: {r2.json()}")
    assert r2.status_code == 403

    # Очистка
    if r1.status_code == 201:
        writer_id = r1.json().get("id")
        requests.delete(f"{BASE_URL}/writers/{writer_id}")


if __name__ == "__main__":
    test_duplicate_writer()
    print("Тест пройден!")