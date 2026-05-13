import pytest
import requests
import json

BASE_URL = "http://localhost:24110/api/v1.0"

@pytest.fixture
def base_url():
    return BASE_URL

@pytest.fixture
def sample_writer():
    """Фикстура для создания тестового writer"""
    writer_data = {
        "login": "test.writer@email.com",
        "password": "testpassword123",
        "firstname": "Test",
        "lastname": "Writer"
    }
    response = requests.post(f"{BASE_URL}/writers", json=writer_data)
    return response.json()

@pytest.fixture
def sample_story(sample_writer):
    """Фикстура для создания тестовой story"""
    story_data = {
        "writer_id": sample_writer["id"],
        "title": "Test Story Title",
        "content": "This is a test story content for testing purposes."
    }
    response = requests.post(f"{BASE_URL}/stories", json=story_data)
    return response.json()

@pytest.fixture
def cleanup_writer():
    """Фикстура для очистки writers после теста"""
    ids_created = []
    yield ids_created
    for id in ids_created:
        try:
            requests.delete(f"{BASE_URL}/writers/{id}")
        except:
            pass

@pytest.fixture
def cleanup_story():
    """Фикстура для очистки stories после теста"""
    ids_created = []
    yield ids_created
    for id in ids_created:
        try:
            requests.delete(f"{BASE_URL}/stories/{id}")
        except:
            pass