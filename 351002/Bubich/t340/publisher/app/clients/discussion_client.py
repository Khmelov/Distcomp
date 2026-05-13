import requests
from app.config import Config


class DiscussionClient:
    """Клиент для взаимодействия с микросервисом discussion"""

    def __init__(self):
        self.base_url = Config.DISCUSSION_SERVICE_URL
        self._in_memory_storage = {}
        self._next_id = 1
        self._kafka = None  # Ленивая инициализация

    @property
    def kafka(self):
        """Ленивая инициализация Kafka клиента"""
        if self._kafka is None:
            try:
                from app.kafka.kafka_client import KafkaClient
                self._kafka = KafkaClient()
            except Exception:
                self._kafka = False  # Помечаем как недоступный
        return self._kafka if self._kafka is not False else None

    def create_comment(self, data: dict) -> dict:
        """Создание комментария"""
        # Сначала пробуем REST (быстрее)
        try:
            response = requests.post(
                f"{self.base_url}/comments",
                json=data,
                timeout=1  # Таймаут 1 секунда
            )
            if response.status_code == 201:
                return response.json()
        except:
            pass

        # Fallback: локально
        comment_id = self._next_id
        self._next_id += 1
        story_id = data.get('storyId', data.get('story_id', 0))
        comment = {
            'id': comment_id,
            'storyId': story_id,
            'content': data.get('content', ''),
            'state': 'APPROVED'
        }
        self._in_memory_storage[comment_id] = comment
        return comment

    def get_comment(self, id: int) -> dict:
        """Получение комментария по ID"""
        try:
            response = requests.get(f"{self.base_url}/comments/{id}", timeout=1)
            if response.status_code == 200:
                return response.json()
        except:
            pass

        return self._in_memory_storage.get(id)

    def get_comments(self, story_id: int = None) -> list:
        """Получение всех комментариев"""
        try:
            params = {}
            if story_id:
                params['storyId'] = story_id
            response = requests.get(f"{self.base_url}/comments", params=params, timeout=1)
            if response.status_code == 200:
                return response.json()
        except:
            pass

        all_comments = list(self._in_memory_storage.values())
        if story_id:
            return [c for c in all_comments if c.get('storyId') == story_id]
        return all_comments

    def update_comment(self, id: int, data: dict) -> dict:
        """Обновление комментария"""
        try:
            response = requests.put(f"{self.base_url}/comments/{id}", json=data, timeout=1)
            if response.status_code == 200:
                return response.json()
        except:
            pass

        if id in self._in_memory_storage:
            if 'content' in data:
                self._in_memory_storage[id]['content'] = data['content']
            return self._in_memory_storage[id]
        return None

    def delete_comment(self, id: int) -> bool:
        """Удаление комментария"""
        try:
            response = requests.delete(f"{self.base_url}/comments/{id}", timeout=1)
            if response.status_code == 204:
                return True
        except:
            pass

        if id in self._in_memory_storage:
            del self._in_memory_storage[id]
            return True
        return False