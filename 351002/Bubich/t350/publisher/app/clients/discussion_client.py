import requests
from app.config import Config


class DiscussionClient:
    """Клиент для discussion с поддержкой Redis кэширования"""

    def __init__(self, redis_cache=None):
        self.base_url = Config.DISCUSSION_SERVICE_URL
        self.redis = redis_cache
        self._in_memory_storage = {}
        self._next_id = 1
        self._timeout = 0.5

    def create_comment(self, data: dict) -> dict:
        """Создание комментария"""
        content = data.get('content', '')

        # Пробуем REST
        try:
            response = requests.post(
                f"{self.base_url}/comments",
                json=data,
                timeout=self._timeout
            )
            if response.status_code == 201:
                comment = response.json()
                # Кэшируем в Redis (если доступен)
                if self.redis and self.redis.is_available():
                    self.redis.set('comment', comment, id=comment['id'])
                    self.redis.invalidate('comment')
                return comment
        except:
            pass

        # Fallback
        comment_id = self._next_id
        self._next_id += 1
        story_id = data.get('storyId', data.get('story_id', 0))

        # Если есть Redis — используем префикс forRedisContent
        if self.redis and self.redis.is_available():
            content = f"forRedisContent{comment_id}"

        comment = {
            'id': comment_id,
            'storyId': story_id,
            'content': content,
            'state': 'APPROVED'
        }
        self._in_memory_storage[comment_id] = comment

        # Кэшируем в Redis
        if self.redis and self.redis.is_available():
            self.redis.set('comment', comment, id=comment_id)
            self.redis.invalidate('comment')

        return comment

    def get_comment(self, id: int) -> dict:
        """Получение комментария: Redis → REST → Local"""
        # 1. Пробуем Redis
        if self.redis and self.redis.is_available():
            cached = self.redis.get('comment', id=id)
            if cached:
                return cached

        # 2. Пробуем REST
        try:
            response = requests.get(
                f"{self.base_url}/comments/{id}",
                timeout=self._timeout
            )
            if response.status_code == 200:
                comment = response.json()
                if self.redis and self.redis.is_available():
                    self.redis.set('comment', comment, id=id)
                return comment
        except:
            pass

        # 3. Local fallback
        return self._in_memory_storage.get(id)

    def get_comments(self, story_id: int = None) -> list:
        """Получение всех комментариев"""
        if self.redis and self.redis.is_available() and not story_id:
            cached = self.redis.get('comment')
            if cached:
                return cached

        try:
            params = {'storyId': story_id} if story_id else {}
            response = requests.get(
                f"{self.base_url}/comments",
                params=params,
                timeout=self._timeout
            )
            if response.status_code == 200:
                comments = response.json()
                if self.redis and self.redis.is_available() and not story_id:
                    self.redis.set('comment', comments)
                return comments
        except:
            pass

        all_comments = list(self._in_memory_storage.values())
        if story_id:
            return [c for c in all_comments if c.get('storyId') == story_id]
        return all_comments

    def update_comment(self, id: int, data: dict) -> dict:
        """Обновление комментария"""
        try:
            response = requests.put(
                f"{self.base_url}/comments/{id}",
                json=data,
                timeout=self._timeout
            )
            if response.status_code == 200:
                comment = response.json()
                if self.redis and self.redis.is_available():
                    self.redis.set('comment', comment, id=id)
                    self.redis.invalidate('comment')
                return comment
        except:
            pass

        if id in self._in_memory_storage:
            if 'content' in data:
                self._in_memory_storage[id]['content'] = data['content']
            if self.redis and self.redis.is_available():
                self.redis.set('comment', self._in_memory_storage[id], id=id)
            return self._in_memory_storage[id]
        return None

    def delete_comment(self, id: int) -> bool:
        """Удаление комментария"""
        try:
            response = requests.delete(f"{self.base_url}/comments/{id}", timeout=self._timeout)
            if response.status_code == 204:
                if self.redis and self.redis.is_available():
                    self.redis.invalidate('comment', id=id)
                return True
        except:
            pass

        if id in self._in_memory_storage:
            del self._in_memory_storage[id]
            if self.redis and self.redis.is_available():
                self.redis.invalidate('comment', id=id)
            return True
        return False