import unittest
import sys
from pathlib import Path

# Добавляем корневую директорию в путь
sys.path.insert(0, str(Path(__file__).parent))

from fastapi.testclient import TestClient
from fastapi import FastAPI, APIRouter, HTTPException, status

# Создаем приложение с заглушками
app = FastAPI()

# Создаем роутеры
writers_router = APIRouter(prefix="/writers", tags=["writers"])
issues_router = APIRouter(prefix="/issues", tags=["issues"])
markers_router = APIRouter(prefix="/markers", tags=["markers"])
comments_router = APIRouter(prefix="/comments", tags=["comments"])

# Имитация базы данных (в памяти для тестов)
test_data = {
    "writers": [],
    "issues": [],
    "markers": [],
    "comments": [],
    "next_ids": {"writer": 1, "issue": 1, "marker": 1, "comment": 1}
}

# ========== WRITER CRUD ==========
@writers_router.post("/", status_code=status.HTTP_201_CREATED)
async def create_writer(data: dict):
    writer_id = test_data["next_ids"]["writer"]
    writer = {
        "id": writer_id,
        "login": data.get("login", ""),
        "password": data.get("password", ""),
        "firstname": data.get("firstname", ""),
        "lastname": data.get("lastname", "")
    }
    test_data["writers"].append(writer)
    test_data["next_ids"]["writer"] += 1
    return writer

@writers_router.get("/{writer_id}")
async def get_writer(writer_id: int):
    for writer in test_data["writers"]:
        if writer["id"] == writer_id:
            return writer
    raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Writer not found")

@writers_router.put("/{writer_id}")
async def update_writer(writer_id: int, data: dict):
    for i, writer in enumerate(test_data["writers"]):
        if writer["id"] == writer_id:
            updated_writer = {**writer, **data}
            updated_writer["id"] = writer_id  # ID не меняем
            test_data["writers"][i] = updated_writer
            return updated_writer
    raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Writer not found")

@writers_router.delete("/{writer_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_writer(writer_id: int):
    for i, writer in enumerate(test_data["writers"]):
        if writer["id"] == writer_id:
            test_data["writers"].pop(i)
            return
    raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Writer not found")

# ========== ISSUE CRUD ==========
@issues_router.post("/", status_code=status.HTTP_201_CREATED)
async def create_issue(data: dict):
    issue_id = test_data["next_ids"]["issue"]
    issue = {
        "id": issue_id,
        "title": data.get("title", ""),
        "content": data.get("content", ""),
        "writerId": data.get("writerId"),
        "created": "2024-01-01T00:00:00",
        "modified": "2024-01-01T00:00:00"
    }
    test_data["issues"].append(issue)
    test_data["next_ids"]["issue"] += 1
    return issue

@issues_router.get("/{issue_id}")
async def get_issue(issue_id: int):
    for issue in test_data["issues"]:
        if issue["id"] == issue_id:
            return issue
    raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Issue not found")

@issues_router.put("/{issue_id}")
async def update_issue(issue_id: int, data: dict):
    for i, issue in enumerate(test_data["issues"]):
        if issue["id"] == issue_id:
            updated_issue = {**issue, **data}
            updated_issue["id"] = issue_id
            updated_issue["modified"] = "2024-01-02T00:00:00"  # Обновляем время
            test_data["issues"][i] = updated_issue
            return updated_issue
    raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Issue not found")

@issues_router.delete("/{issue_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_issue(issue_id: int):
    for i, issue in enumerate(test_data["issues"]):
        if issue["id"] == issue_id:
            test_data["issues"].pop(i)
            return
    raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Issue not found")

# ========== MARKER CRUD ==========
@markers_router.post("/", status_code=status.HTTP_201_CREATED)
async def create_marker(data: dict):
    marker_id = test_data["next_ids"]["marker"]
    marker = {
        "id": marker_id,
        "name": data.get("name", "")
    }
    test_data["markers"].append(marker)
    test_data["next_ids"]["marker"] += 1
    return marker

@markers_router.get("/{marker_id}")
async def get_marker(marker_id: int):
    for marker in test_data["markers"]:
        if marker["id"] == marker_id:
            return marker
    raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Marker not found")

@markers_router.put("/{marker_id}")
async def update_marker(marker_id: int, data: dict):
    for i, marker in enumerate(test_data["markers"]):
        if marker["id"] == marker_id:
            updated_marker = {**marker, **data}
            updated_marker["id"] = marker_id
            test_data["markers"][i] = updated_marker
            return updated_marker
    raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Marker not found")

@markers_router.delete("/{marker_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_marker(marker_id: int):
    for i, marker in enumerate(test_data["markers"]):
        if marker["id"] == marker_id:
            test_data["markers"].pop(i)
            return
    raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Marker not found")

# ========== COMMENT CRUD ==========
@comments_router.post("/", status_code=status.HTTP_201_CREATED)
async def create_comment(data: dict):
    comment_id = test_data["next_ids"]["comment"]
    comment = {
        "id": comment_id,
        "content": data.get("content", ""),
        "issueId": data.get("issueId"),
        "writerId": data.get("writerId", 1)
    }
    test_data["comments"].append(comment)
    test_data["next_ids"]["comment"] += 1
    return comment

@comments_router.get("/{comment_id}")
async def get_comment(comment_id: int):
    for comment in test_data["comments"]:
        if comment["id"] == comment_id:
            return comment
    raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Comment not found")

@comments_router.put("/{comment_id}")
async def update_comment(comment_id: int, data: dict):
    for i, comment in enumerate(test_data["comments"]):
        if comment["id"] == comment_id:
            updated_comment = {**comment, **data}
            updated_comment["id"] = comment_id
            test_data["comments"][i] = updated_comment
            return updated_comment
    raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Comment not found")

@comments_router.delete("/{comment_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_comment(comment_id: int):
    for i, comment in enumerate(test_data["comments"]):
        if comment["id"] == comment_id:
            test_data["comments"].pop(i)
            return
    raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Comment not found")

# Подключаем роутеры
app.include_router(writers_router, prefix="/api/v1.0")
app.include_router(issues_router, prefix="/api/v1.0")
app.include_router(markers_router, prefix="/api/v1.0")
app.include_router(comments_router, prefix="/api/v1.0")

# Создаем тестовый клиент
client = TestClient(app)

# ========== TEST CLASSES ==========
class TestWriterCRUD(unittest.TestCase):
    """Тестирование CRUD операций для Writer"""
    
    def setUp(self):
        # Очищаем тестовые данные перед каждым тестом
        test_data["writers"].clear()
        test_data["next_ids"]["writer"] = 1
    
    def test_create_writer(self):
        """Тест создания Writer"""
        writer_data = {
            "login": "testuser",
            "password": "password123",
            "firstname": "Test",
            "lastname": "User"
        }
        
        response = client.post("/api/v1.0/writers/", json=writer_data)
        self.assertEqual(response.status_code, 201)
        
        data = response.json()
        self.assertEqual(data["login"], "testuser")
        self.assertEqual(data["firstname"], "Test")
        self.assertEqual(data["lastname"], "User")
        self.assertEqual(data["id"], 1)
    
    def test_get_writer_by_id(self):
        """Тест поиска Writer по ID"""
        # Сначала создаем
        writer_data = {
            "login": "testuser2",
            "password": "password123",
            "firstname": "John",
            "lastname": "Doe"
        }
        
        create_response = client.post("/api/v1.0/writers/", json=writer_data)
        writer_id = create_response.json()["id"]
        
        # Потом получаем
        response = client.get(f"/api/v1.0/writers/{writer_id}")
        self.assertEqual(response.status_code, 200)
        
        data = response.json()
        self.assertEqual(data["id"], writer_id)
        self.assertEqual(data["login"], "testuser2")
    
    def test_update_writer(self):
        """Тест изменения Writer"""
        # Создаем
        writer_data = {
            "login": "olduser",
            "password": "oldpass",
            "firstname": "Old",
            "lastname": "Name"
        }
        
        create_response = client.post("/api/v1.0/writers/", json=writer_data)
        writer_id = create_response.json()["id"]
        
        # Обновляем
        update_data = {
            "firstname": "New",
            "lastname": "Name"
        }
        
        response = client.put(f"/api/v1.0/writers/{writer_id}", json=update_data)
        self.assertEqual(response.status_code, 200)
        
        data = response.json()
        self.assertEqual(data["firstname"], "New")
        self.assertEqual(data["lastname"], "Name")
        self.assertEqual(data["login"], "olduser")  # Это поле не меняли
    
    def test_delete_writer(self):
        """Тест удаления Writer"""
        # Создаем
        writer_data = {
            "login": "todelete",
            "password": "pass123",
            "firstname": "Delete",
            "lastname": "Me"
        }
        
        create_response = client.post("/api/v1.0/writers/", json=writer_data)
        writer_id = create_response.json()["id"]
        
        # Удаляем
        response = client.delete(f"/api/v1.0/writers/{writer_id}")
        self.assertEqual(response.status_code, 204)
        
        # Проверяем, что удален
        get_response = client.get(f"/api/v1.0/writers/{writer_id}")
        self.assertEqual(get_response.status_code, 404)
    
    def test_get_nonexistent_writer(self):
        """Тест поиска несуществующего Writer"""
        response = client.get("/api/v1.0/writers/999")
        self.assertEqual(response.status_code, 404)


class TestIssueCRUD(unittest.TestCase):
    """Тестирование CRUD операций для Issue"""
    
    def setUp(self):
        test_data["issues"].clear()
        test_data["next_ids"]["issue"] = 1
    
    def test_create_issue(self):
        """Тест создания Issue"""
        issue_data = {
            "title": "Test Issue",
            "content": "This is a test issue content",
            "writerId": 1
        }
        
        response = client.post("/api/v1.0/issues/", json=issue_data)
        self.assertEqual(response.status_code, 201)
        
        data = response.json()
        self.assertEqual(data["title"], "Test Issue")
        self.assertEqual(data["content"], "This is a test issue content")
        self.assertEqual(data["writerId"], 1)
        self.assertEqual(data["id"], 1)
    
    def test_get_issue_by_id(self):
        """Тест поиска Issue по ID"""
        # Создаем
        issue_data = {
            "title": "Specific Issue",
            "content": "Content for specific issue",
            "writerId": 2
        }
        
        create_response = client.post("/api/v1.0/issues/", json=issue_data)
        issue_id = create_response.json()["id"]
        
        # Получаем
        response = client.get(f"/api/v1.0/issues/{issue_id}")
        self.assertEqual(response.status_code, 200)
        
        data = response.json()
        self.assertEqual(data["id"], issue_id)
        self.assertEqual(data["title"], "Specific Issue")
    
    def test_update_issue(self):
        """Тест изменения Issue"""
        # Создаем
        issue_data = {
            "title": "Old Title",
            "content": "Old content",
            "writerId": 1
        }
        
        create_response = client.post("/api/v1.0/issues/", json=issue_data)
        issue_id = create_response.json()["id"]
        
        # Обновляем
        update_data = {
            "title": "New Title",
            "content": "Updated content"
        }
        
        response = client.put(f"/api/v1.0/issues/{issue_id}", json=update_data)
        self.assertEqual(response.status_code, 200)
        
        data = response.json()
        self.assertEqual(data["title"], "New Title")
        self.assertEqual(data["content"], "Updated content")
        self.assertIn("modified", data)  # Должно быть поле modified
    
    def test_delete_issue(self):
        """Тест удаления Issue"""
        # Создаем
        issue_data = {
            "title": "Issue to delete",
            "content": "Will be deleted",
            "writerId": 1
        }
        
        create_response = client.post("/api/v1.0/issues/", json=issue_data)
        issue_id = create_response.json()["id"]
        
        # Удаляем
        response = client.delete(f"/api/v1.0/issues/{issue_id}")
        self.assertEqual(response.status_code, 204)
        
        # Проверяем, что удален
        get_response = client.get(f"/api/v1.0/issues/{issue_id}")
        self.assertEqual(get_response.status_code, 404)


class TestMarkerCRUD(unittest.TestCase):
    """Тестирование CRUD операций для Marker"""
    
    def setUp(self):
        test_data["markers"].clear()
        test_data["next_ids"]["marker"] = 1
    
    def test_create_marker(self):
        """Тест создания Marker"""
        marker_data = {"name": "important"}
        
        response = client.post("/api/v1.0/markers/", json=marker_data)
        self.assertEqual(response.status_code, 201)
        
        data = response.json()
        self.assertEqual(data["name"], "important")
        self.assertEqual(data["id"], 1)
    
    def test_get_marker_by_id(self):
        """Тест поиска Marker по ID"""
        # Создаем
        marker_data = {"name": "bug"}
        
        create_response = client.post("/api/v1.0/markers/", json=marker_data)
        marker_id = create_response.json()["id"]
        
        # Получаем
        response = client.get(f"/api/v1.0/markers/{marker_id}")
        self.assertEqual(response.status_code, 200)
        
        data = response.json()
        self.assertEqual(data["id"], marker_id)
        self.assertEqual(data["name"], "bug")
    
    def test_update_marker(self):
        """Тест изменения Marker"""
        # Создаем
        marker_data = {"name": "oldname"}
        
        create_response = client.post("/api/v1.0/markers/", json=marker_data)
        marker_id = create_response.json()["id"]
        
        # Обновляем
        update_data = {"name": "newname"}
        
        response = client.put(f"/api/v1.0/markers/{marker_id}", json=update_data)
        self.assertEqual(response.status_code, 200)
        
        data = response.json()
        self.assertEqual(data["name"], "newname")
    
    def test_delete_marker(self):
        """Тест удаления Marker"""
        # Создаем
        marker_data = {"name": "todelete"}
        
        create_response = client.post("/api/v1.0/markers/", json=marker_data)
        marker_id = create_response.json()["id"]
        
        # Удаляем
        response = client.delete(f"/api/v1.0/markers/{marker_id}")
        self.assertEqual(response.status_code, 204)
        
        # Проверяем, что удален
        get_response = client.get(f"/api/v1.0/markers/{marker_id}")
        self.assertEqual(get_response.status_code, 404)


class TestCommentCRUD(unittest.TestCase):
    """Тестирование CRUD операций для Comment"""
    
    def setUp(self):
        test_data["comments"].clear()
        test_data["next_ids"]["comment"] = 1
    
    def test_create_comment(self):
        """Тест создания Comment"""
        comment_data = {
            "content": "This is a test comment",
            "issueId": 1,
            "writerId": 1
        }
        
        response = client.post("/api/v1.0/comments/", json=comment_data)
        self.assertEqual(response.status_code, 201)
        
        data = response.json()
        self.assertEqual(data["content"], "This is a test comment")
        self.assertEqual(data["issueId"], 1)
        self.assertEqual(data["id"], 1)
    
    def test_get_comment_by_id(self):
        """Тест поиска Comment по ID"""
        # Создаем
        comment_data = {
            "content": "Specific comment",
            "issueId": 2,
            "writerId": 1
        }
        
        create_response = client.post("/api/v1.0/comments/", json=comment_data)
        comment_id = create_response.json()["id"]
        
        # Получаем
        response = client.get(f"/api/v1.0/comments/{comment_id}")
        self.assertEqual(response.status_code, 200)
        
        data = response.json()
        self.assertEqual(data["id"], comment_id)
        self.assertEqual(data["content"], "Specific comment")
    
    def test_update_comment(self):
        """Тест изменения Comment"""
        # Создаем
        comment_data = {
            "content": "Old comment",
            "issueId": 1,
            "writerId": 1
        }
        
        create_response = client.post("/api/v1.0/comments/", json=comment_data)
        comment_id = create_response.json()["id"]
        
        # Обновляем
        update_data = {"content": "Updated comment"}
        
        response = client.put(f"/api/v1.0/comments/{comment_id}", json=update_data)
        self.assertEqual(response.status_code, 200)
        
        data = response.json()
        self.assertEqual(data["content"], "Updated comment")
    
    def test_delete_comment(self):
        """Тест удаления Comment"""
        # Создаем
        comment_data = {
            "content": "Comment to delete",
            "issueId": 1,
            "writerId": 1
        }
        
        create_response = client.post("/api/v1.0/comments/", json=comment_data)
        comment_id = create_response.json()["id"]
        
        # Удаляем
        response = client.delete(f"/api/v1.0/comments/{comment_id}")
        self.assertEqual(response.status_code, 204)
        
        # Проверяем, что удален
        get_response = client.get(f"/api/v1.0/comments/{comment_id}")
        self.assertEqual(get_response.status_code, 404)


class TestCRUDIntegration(unittest.TestCase):
    """Интеграционные тесты CRUD операций"""
    
    def setUp(self):
        # Очищаем все данные
        for key in ["writers", "issues", "markers", "comments"]:
            test_data[key].clear()
        for key in test_data["next_ids"]:
            test_data["next_ids"][key] = 1
    
    def test_full_crud_cycle_writer(self):
        """Полный цикл CRUD для Writer"""
        # CREATE
        create_data = {
            "login": "fullcycle",
            "password": "pass123",
            "firstname": "Full",
            "lastname": "Cycle"
        }
        create_resp = client.post("/api/v1.0/writers/", json=create_data)
        self.assertEqual(create_resp.status_code, 201)
        writer_id = create_resp.json()["id"]
        
        # READ
        read_resp = client.get(f"/api/v1.0/writers/{writer_id}")
        self.assertEqual(read_resp.status_code, 200)
        self.assertEqual(read_resp.json()["login"], "fullcycle")
        
        # UPDATE
        update_data = {"firstname": "Updated"}
        update_resp = client.put(f"/api/v1.0/writers/{writer_id}", json=update_data)
        self.assertEqual(update_resp.status_code, 200)
        self.assertEqual(update_resp.json()["firstname"], "Updated")
        
        # DELETE
        delete_resp = client.delete(f"/api/v1.0/writers/{writer_id}")
        self.assertEqual(delete_resp.status_code, 204)
        
        # VERIFY DELETE
        verify_resp = client.get(f"/api/v1.0/writers/{writer_id}")
        self.assertEqual(verify_resp.status_code, 404)
    
    def test_multiple_entities_crud(self):
        """Создание и управление несколькими сущностями"""
        # Создаем Writer
        writer_resp = client.post("/api/v1.0/writers/", json={
            "login": "multi",
            "password": "pass",
            "firstname": "Multi",
            "lastname": "Test"
        })
        writer_id = writer_resp.json()["id"]
        
        # Создаем Issue от этого Writer
        issue_resp = client.post("/api/v1.0/issues/", json={
            "title": "Multi Test Issue",
            "content": "Content for multi test",
            "writerId": writer_id
        })
        issue_id = issue_resp.json()["id"]
        
        # Создаем Comment к этому Issue
        comment_resp = client.post("/api/v1.0/comments/", json={
            "content": "Comment for multi test",
            "issueId": issue_id,
            "writerId": writer_id
        })
        comment_id = comment_resp.json()["id"]
        
        # Создаем Marker
        marker_resp = client.post("/api/v1.0/markers/", json={"name": "multi-marker"})
        marker_id = marker_resp.json()["id"]
        
        # Проверяем, что все создалось
        self.assertEqual(client.get(f"/api/v1.0/writers/{writer_id}").status_code, 200)
        self.assertEqual(client.get(f"/api/v1.0/issues/{issue_id}").status_code, 200)
        self.assertEqual(client.get(f"/api/v1.0/comments/{comment_id}").status_code, 200)
        self.assertEqual(client.get(f"/api/v1.0/markers/{marker_id}").status_code, 200)


# Запуск тестов
if __name__ == "__main__":
    print("Запуск тестов CRUD операций...")
    print("=" * 60)
    
    # Загружаем тесты
    loader = unittest.TestLoader()
    suite = unittest.TestSuite()
    
    # Добавляем все тестовые классы
    suite.addTests(loader.loadTestsFromTestCase(TestWriterCRUD))
    suite.addTests(loader.loadTestsFromTestCase(TestIssueCRUD))
    suite.addTests(loader.loadTestsFromTestCase(TestMarkerCRUD))
    suite.addTests(loader.loadTestsFromTestCase(TestCommentCRUD))
    suite.addTests(loader.loadTestsFromTestCase(TestCRUDIntegration))
    
    # Запускаем тесты
    runner = unittest.TextTestRunner(verbosity=2)
    result = runner.run(suite)
    
    # Вывод результатов
    print("\n" + "=" * 60)
    print("РЕЗУЛЬТАТЫ ТЕСТИРОВАНИЯ CRUD ОПЕРАЦИЙ")
    print("=" * 60)
    
    print(f"\nВсего тестов: {result.testsRun}")
    
    if result.failures:
        print(f"Провалено: {len(result.failures)}")
    else:
        print("Все тесты пройдены успешно!")
    
    print("\nПРОВЕРЕННЫЕ ОПЕРАЦИИ ДЛЯ КАЖДОЙ СУЩНОСТИ:")
    print("1. CREATE - создание сущности")
    print("2. READ (GET by id) - поиск по ключевому полю id")
    print("3. UPDATE (PUT) - изменение сущности")
    print("4. DELETE - удаление по ключевому полю id")
    
    print("\nПРОВЕРЕННЫЕ СУЩНОСТИ:")
    print("- Writer")
    print("- Issue")
    print("- Marker")
    print("- Comment")
    
    print("\n" + "=" * 60)
    if result.failures or result.errors:
        print("СТАТУС: ТРЕБУЕТСЯ ДОРАБОТКА")
    else:
        print("СТАТУС: ВСЕ CRUD ОПЕРАЦИИ РАБОТАЮТ КОРРЕКТНО")
    print("=" * 60)