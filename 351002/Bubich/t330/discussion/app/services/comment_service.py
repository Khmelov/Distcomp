from typing import List, Dict, Any
from app.repository.cassandra_repository import CassandraRepository
from werkzeug.exceptions import NotFound, BadRequest


class CommentService:
    def __init__(self, repository: CassandraRepository):
        self.repository = repository

    def create(self, data: Dict[str, Any]):
        self._validate(data)

        # Генерируем ID если не передан
        if 'id' not in data:
            data['id'] = self._generate_id()

        return self.repository.save(data)

    def get_by_id(self, id: int):
        comment = self.repository.find_by_id(id)
        if not comment:
            raise NotFound(f"Comment with id {id} not found")
        return comment

    def get_by_story_id(self, story_id: int) -> List[Dict]:
        return self.repository.find_by_story_id(story_id)

    def get_all(self):
        return self.repository.find_all()

    def update(self, id: int, data: Dict[str, Any]):
        self._validate(data)

        existing = self.repository.find_by_id(id)
        if not existing:
            raise NotFound(f"Comment with id {id} not found")

        return self.repository.update(id, data)

    def delete(self, id: int):
        comment = self.repository.find_by_id(id)
        if not comment:
            raise NotFound(f"Comment with id {id} not found")

        self.repository.delete_by_id(id, comment.get('story_id'))

    def _validate(self, data: Dict[str, Any]):
        content = data.get('content', '')
        if len(content) < 2 or len(content) > 2048:
            raise BadRequest("Content must be between 2 and 2048 characters")

    def _generate_id(self):
        import time
        return int(time.time() * 1000) % (10 ** 10)