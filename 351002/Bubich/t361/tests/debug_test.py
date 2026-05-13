import requests
import json

BASE_URL = "http://localhost:24110/api/v1.0"


def test_403_for_nonexistent_writer():
    print("=" * 60)
    print("ТЕСТ: Создание Story с несуществующим writer")
    print("=" * 60)

    # 1. Получаем всех writers
    writers = requests.get(f"{BASE_URL}/writers").json()
    print(f"\n1. Всего writers в БД: {len(writers)}")

    if writers:
        max_id = max([w['id'] for w in writers])
        print(f"   Максимальный ID: {max_id}")
        print(f"   Все ID: {[w['id'] for w in writers]}")
    else:
        max_id = 0
        print("   Writers нет в БД")

    # 2. Берем заведомо несуществующий ID
    test_writer_id = max_id + 100  # Точно не существует

    print(f"\n2. Пробуем создать Story с writerId={test_writer_id}")

    story_data = {
        "writerId": test_writer_id,
        "title": "Test Story for 403",
        "content": "This content should trigger 403 error"
    }

    response = requests.post(f"{BASE_URL}/stories", json=story_data)

    print(f"\n3. Ответ сервера:")
    print(f"   HTTP Status: {response.status_code}")
    print(f"   Body: {json.dumps(response.json(), indent=2, ensure_ascii=False)}")

    # 3. Проверка результата
    print(f"\n4. Результат проверки:")
    if response.status_code == 403:
        print("   ✅ ТЕСТ ПРОЙДЕН: Получен статус 403")
        return True
    elif response.status_code == 201:
        print("   ❌ ТЕСТ НЕ ПРОЙДЕН: Story создана (201), хотя writer не существует!")

        # Проверим, может writer с таким ID всё-таки есть
        check_writer = requests.get(f"{BASE_URL}/writers/{test_writer_id}")
        if check_writer.status_code == 200:
            print(f"   ⚠️ Writer {test_writer_id} СУЩЕСТВУЕТ: {check_writer.json()['login']}")
        else:
            print(f"   ⚠️ Writer {test_writer_id} не существует, но Story создана! БАГ!")

        # Удалим ошибочно созданную story
        if 'id' in response.json():
            story_id = response.json()['id']
            requests.delete(f"{BASE_URL}/stories/{story_id}")
            print(f"   🗑️ Ошибочная Story {story_id} удалена")

        return False
    else:
        print(f"   ❌ Неожиданный статус: {response.status_code}")
        return False


def test_201_for_existing_writer():
    print("\n" + "=" * 60)
    print("ТЕСТ: Создание Story с существующим writer")
    print("=" * 60)

    # Получаем первого writer
    writers = requests.get(f"{BASE_URL}/writers").json()
    if not writers:
        print("❌ Нет writers для теста. Сначала создайте writer.")
        return False

    first_writer_id = writers[0]['id']
    print(f"\n1. Используем writer с ID={first_writer_id}")

    story_data = {
        "writerId": first_writer_id,
        "title": "Valid Story Test",
        "content": "This is a valid story with existing writer"
    }

    response = requests.post(f"{BASE_URL}/stories", json=story_data)

    print(f"\n2. Ответ сервера:")
    print(f"   HTTP Status: {response.status_code}")

    if response.status_code == 201:
        print("   ✅ ТЕСТ ПРОЙДЕН: Story создана с кодом 201")
        # Очистка
        story_id = response.json().get('id')
        if story_id:
            requests.delete(f"{BASE_URL}/stories/{story_id}")
            print(f"   🗑️ Тестовая Story {story_id} удалена")
        return True
    else:
        print(f"   ❌ Ошибка: {response.json()}")
        return False


if __name__ == "__main__":
    print("Проверка работы 403 Forbidden для Story\n")

    # Проверяем что сервер запущен
    try:
        requests.get(f"{BASE_URL}/writers", timeout=2)
    except:
        print("❌ Сервер не запущен! Запустите: python run_db.py")
        exit(1)

    result1 = test_403_for_nonexistent_writer()
    result2 = test_201_for_existing_writer()

    print("\n" + "=" * 60)
    if result1 and result2:
        print("✅ ВСЕ ТЕСТЫ ПРОЙДЕНЫ!")
    else:
        print("❌ ЕСТЬ ПРОБЛЕМЫ! Проверьте логи сервера.")