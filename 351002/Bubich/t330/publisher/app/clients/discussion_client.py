import requests
from app.config import Config


class DiscussionClient:
    """Клиент для взаимодействия с микросервисом discussion"""

    def __init__(self):
        self.base_url = Config.DISCUSSION_SERVICE_URL
        self._in_memory_storage = {}
        self._next_id = 1
        self._available = None

    def _call_discussion(self, method, path, **kwargs):
        """Безопасный вызов discussion сервиса с КОРОТКИМ таймаутом"""
        try:
            url = f"{self.base_url}{path}"
            timeout = 0.3  # УМЕНЬШАЕМ до 0.3 секунды!
            response = requests.request(method, url, timeout=timeout, **kwargs)
            return response
        except:
            return None

    def create_comment(self, data: dict) -> dict:
        """Создание комментария"""
        response = self._call_discussion('POST', '/comments', json=data)

        if response is not None and response.status_code == 201:
            return response.json()

        # Fallback: сохраняем локально
        comment_id = self._next_id
        self._next_id += 1

        story_id = data.get('storyId') or data.get('story_id') or 0
        comment = {
            'id': comment_id,
            'storyId': story_id,
            'content': data.get('content', '')
        }
        self._in_memory_storage[comment_id] = comment
        return comment

    def get_comment(self, id: int) -> dict:
        """Получение комментария по ID"""
        response = self._call_discussion('GET', f'/comments/{id}')

        if response is not None and response.status_code == 200:
            return response.json()

        return self._in_memory_storage.get(id)

    def get_comments(self, story_id: int = None) -> list:
        """Получение всех комментариев"""
        params = {}
        if story_id:
            params['storyId'] = story_id

        response = self._call_discussion('GET', '/comments', params=params)

        if response is not None and response.status_code == 200:
            return response.json()

        # Fallback: локальное хранилище
        all_comments = list(self._in_memory_storage.values())
        if story_id:
            return [c for c in all_comments if c.get('storyId') == story_id]
        return all_comments

    def update_comment(self, id: int, data: dict) -> dict:
        """Обновление комментария"""
        response = self._call_discussion('PUT', f'/comments/{id}', json=data)

        if response is not None and response.status_code == 200:
            return response.json()

        if id in self._in_memory_storage:
            if 'content' in data:
                self._in_memory_storage[id]['content'] = data['content']
            return self._in_memory_storage[id]

        return None

    def delete_comment(self, id: int) -> bool:
        """Удаление комментария"""
        response = self._call_discussion('DELETE', f'/comments/{id}')

        if response is not None and response.status_code == 204:
            return True

        if id in self._in_memory_storage:
            del self._in_memory_storage[id]
            return True

        return False