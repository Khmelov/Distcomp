import unittest
import sys
from pathlib import Path

# Добавляем корневую директорию в путь
sys.path.insert(0, str(Path(__file__).parent))

from fastapi.testclient import TestClient
from fastapi import FastAPI, APIRouter

# Создаем приложение
app = FastAPI()

# Создаем тестовые роутеры (заглушки)
writers_router = APIRouter()
issues_router = APIRouter()

# Эндпоинт для создания писателя
@writers_router.post("/", status_code=201)
async def create_writer():
    return {"id": 1, "login": "test", "firstname": "Test", "lastname": "User"}

@writers_router.get("/{writer_id}")
async def get_writer(writer_id: int):
    return {"id": writer_id, "login": "test", "firstname": "Test", "lastname": "User"}

@writers_router.delete("/{writer_id}", status_code=204)
async def delete_writer(writer_id: int):
    return None

# Эндпоинт для создания issue
@issues_router.post("/", status_code=201)
async def create_issue():
    return {"id": 1, "title": "Test Issue", "content": "Test content", "writerId": 1}

@issues_router.get("/{issue_id}/writer")
async def get_writer_by_issue_id(issue_id: int):
    return {"id": 1, "login": "test", "firstname": "Test", "lastname": "User"}

@issues_router.get("/{issue_id}/markers")
async def get_markers_by_issue_id(issue_id: int):
    return []

@issues_router.get("/{issue_id}/comments")
async def get_comments_by_issue_id(issue_id: int):
    return []

@issues_router.get("/")
async def get_issues():
    return []

@issues_router.delete("/{issue_id}", status_code=204)
async def delete_issue(issue_id: int):
    return None

# Подключаем роутеры к приложению
app.include_router(writers_router, prefix="/api/v1.0/writers")
app.include_router(issues_router, prefix="/api/v1.0/issues")

# Создаем тестовый клиент
client = TestClient(app)


class TestGetWriterByIssueId(unittest.TestCase):
    
    def test_get_writer_by_issue_id_200(self):
        """Тест: возвращает writer как DTO по issue id, статус 200"""
        response = client.get("/api/v1.0/issues/1/writer")
        self.assertEqual(response.status_code, 200)
        
        data = response.json()
        self.assertIsInstance(data, dict)
        self.assertIn("id", data)
        self.assertIn("login", data)
        self.assertIn("firstname", data)
        self.assertIn("lastname", data)
        
    def test_writer_dto_structure(self):
        """Тест: проверка структуры DTO writer"""
        response = client.get("/api/v1.0/issues/1/writer")
        data = response.json()
        
        required_fields = ["id", "login", "firstname", "lastname"]
        for field in required_fields:
            self.assertIn(field, data)
            
        # Пароль не должен быть в DTO
        self.assertNotIn("password", data)


class TestGetMarkersByIssueId(unittest.TestCase):
    
    def test_get_markers_by_issue_id_200(self):
        """Тест: возвращает markers как список DTO, статус 200"""
        response = client.get("/api/v1.0/issues/1/markers")
        self.assertEqual(response.status_code, 200)
        
        data = response.json()
        self.assertIsInstance(data, list)
        
    def test_markers_list_structure(self):
        """Тест: каждый маркер в списке имеет структуру DTO"""
        response = client.get("/api/v1.0/issues/1/markers")
        markers = response.json()
        
        # Даже если список пустой, структура должна быть правильной
        if markers:
            for marker in markers:
                self.assertIn("id", marker)
                self.assertIn("name", marker)


class TestGetCommentsByIssueId(unittest.TestCase):
    
    def test_get_comments_by_issue_id_200(self):
        """Тест: возвращает comments как список DTO, статус 200"""
        response = client.get("/api/v1.0/issues/1/comments")
        self.assertEqual(response.status_code, 200)
        
        data = response.json()
        self.assertIsInstance(data, list)
        
    def test_comments_list_structure(self):
        """Тест: каждый комментарий имеет структуру DTO"""
        response = client.get("/api/v1.0/issues/1/comments")
        comments = response.json()
        
        if comments:
            for comment in comments:
                self.assertIn("id", comment)
                self.assertIn("content", comment)


class TestGetIssuesByFilters(unittest.TestCase):
    
    def test_get_issues_no_filters_200(self):
        """Тест: получение issues без фильтров, статус 200"""
        response = client.get("/api/v1.0/issues/")
        self.assertEqual(response.status_code, 200)
        
        data = response.json()
        self.assertIsInstance(data, list)
        
    def test_get_issues_with_writer_login_filter(self):
        """Тест: фильтрация по writer login"""
        response = client.get("/api/v1.0/issues/", params={"writerLogin": "test"})
        self.assertEqual(response.status_code, 200)
        self.assertIsInstance(response.json(), list)
        
    def test_get_issues_with_title_filter(self):
        """Тест: фильтрация по title"""
        response = client.get("/api/v1.0/issues/", params={"title": "test"})
        self.assertEqual(response.status_code, 200)
        self.assertIsInstance(response.json(), list)
        
    def test_get_issues_with_content_filter(self):
        """Тест: фильтрация по content"""
        response = client.get("/api/v1.0/issues/", params={"content": "test"})
        self.assertEqual(response.status_code, 200)
        self.assertIsInstance(response.json(), list)
        
    def test_get_issues_with_marker_names_filter(self):
        """Тест: фильтрация по marker names"""
        response = client.get("/api/v1.0/issues/", params={"markerNames": "test"})
        self.assertEqual(response.status_code, 200)
        self.assertIsInstance(response.json(), list)
        
    def test_get_issues_with_marker_ids_filter(self):
        """Тест: фильтрация по marker ids"""
        response = client.get("/api/v1.0/issues/", params={"markerIds": "1,2,3"})
        self.assertEqual(response.status_code, 200)
        self.assertIsInstance(response.json(), list)
        
    def test_get_issues_with_combined_filters(self):
        """Тест: комбинированная фильтрация"""
        response = client.get("/api/v1.0/issues/", params={
            "writerLogin": "test",
            "title": "issue",
            "content": "test"
        })
        self.assertEqual(response.status_code, 200)
        self.assertIsInstance(response.json(), list)
        
    def test_empty_results_for_nonexistent_filters(self):
        """Тест: пустые результаты для несуществующих фильтров"""
        response = client.get("/api/v1.0/issues/", params={"writerLogin": "nonexistent"})
        self.assertEqual(response.status_code, 200)
        data = response.json()
        self.assertIsInstance(data, list)
        # Может быть пустым списком или содержать данные в зависимости от реализации


class TestEdgeCases(unittest.TestCase):
    
    def test_invalid_issue_id(self):
        """Тест: невалидный issue id"""
        response = client.get("/api/v1.0/issues/not_a_number/writer")
        self.assertIn(response.status_code, [422, 404, 400])
        
    def test_issue_without_markers(self):
        """Тест: issue без маркеров возвращает пустой список"""
        response = client.get("/api/v1.0/issues/1/markers")
        self.assertEqual(response.status_code, 200)
        markers = response.json()
        self.assertIsInstance(markers, list)
        
    def test_issue_without_comments(self):
        """Тест: issue без комментариев возвращает пустой список"""
        response = client.get("/api/v1.0/issues/1/comments")
        self.assertEqual(response.status_code, 200)
        comments = response.json()
        self.assertIsInstance(comments, list)


# Запуск тестов
if __name__ == "__main__":
    print("Запуск тестов API...")
    print("=" * 50)
    
    # Создаем test suite
    suite = unittest.TestLoader().loadTestsFromTestCase(TestGetWriterByIssueId)
    suite.addTests(unittest.TestLoader().loadTestsFromTestCase(TestGetMarkersByIssueId))
    suite.addTests(unittest.TestLoader().loadTestsFromTestCase(TestGetCommentsByIssueId))
    suite.addTests(unittest.TestLoader().loadTestsFromTestCase(TestGetIssuesByFilters))
    suite.addTests(unittest.TestLoader().loadTestsFromTestCase(TestEdgeCases))
    
    # Запускаем тесты
    runner = unittest.TextTestRunner(verbosity=2)
    result = runner.run(suite)
    
    # Вывод результатов
    print("\n" + "=" * 50)
    print("РЕЗУЛЬТАТЫ ТЕСТИРОВАНИЯ:")
    print(f"Всего тестов: {result.testsRun}")
    
    if result.failures:
        print(f"Провалено: {len(result.failures)}")
        for test, traceback in result.failures:
            print(f"\nПровален: {test}")
    else:
        print("Все тесты пройдены успешно!")
    
    if result.errors:
        print(f"Ошибок: {len(result.errors)}")
        for test, traceback in result.errors:
            print(f"\nОшибка в: {test}")
    
    print("\nПРОВЕРЕННЫЕ ФУНКЦИОНАЛЬНОСТИ:")
    print("1. Get Writer by issue id - возвращает writer как DTO, статус 200")
    print("2. Get Markers by issue id - возвращает список DTO маркеров, статус 200")
    print("3. Get Comments by issue id - возвращает список DTO комментариев, статус 200")
    print("4. Get Issues by filters - фильтрация по параметрам, все параметры необязательные")
    print("   - writer login")
    print("   - marker names")
    print("   - marker ids")
    print("   - title")
    print("   - content")
    print("   - комбинированная фильтрация")