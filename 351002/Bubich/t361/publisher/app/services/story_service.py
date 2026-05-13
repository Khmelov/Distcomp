from typing import Dict, Any
from datetime import datetime
from app.models.story_model import StoryModel
from app.models.writer_model import WriterModel
from app.repository.database_repository import DatabaseRepository
from app.exceptions.custom_exceptions import NotFoundError, ValidationError, DuplicateError
from app.cache.redis_cache import RedisCache


class StoryService:
    def __init__(self, repository: DatabaseRepository, writer_repository: DatabaseRepository = None,
                 cache: RedisCache = None):
        self.repository = repository
        self.writer_repository = writer_repository
        self.cache = cache

    def create(self, data: Dict[str, Any]):
        if 'writerId' in data:
            data['writer_id'] = data.pop('writerId')

        self._validate(data)

        writer_id = data.get('writer_id')
        if writer_id:
            writer = WriterModel.query.get(writer_id)
            if not writer:
                raise ValidationError(f"Writer with id {writer_id} does not exist", "40301")

        # Проверка дубликата title
        title = data.get('title')
        if writer_id and title:
            existing = StoryModel.query.filter_by(writer_id=writer_id, title=title).first()
            if existing:
                raise DuplicateError(f"Story with title '{title}' already exists for writer {writer_id}", "40302")

        data['created'] = datetime.utcnow()
        data['modified'] = datetime.utcnow()

        result = self.repository.save(data)

        # Инвалидация кэша
        if self.cache:
            self.cache.invalidate('story')

        return result

    def get_by_id(self, id: int):
        # Проверяем кэш
        if self.cache:
            cached = self.cache.get('story', id=id)
            if cached:
                return cached

        story = self.repository.find_by_id(id)
        if not story:
            raise NotFoundError(f"Story with id {id} not found")

        # Сохраняем в кэш
        if self.cache:
            self.cache.set('story', story.to_dict(), id=id)

        return story

    def get_all(self, pagination=None):
        # Кэшируем только запросы без пагинации
        if self.cache and not pagination:
            cached = self.cache.get('story')
            if cached:
                return {'items': cached, 'total': len(cached)}

        result = self.repository.find_all(pagination)

        if self.cache and not pagination and 'items' in result:
            items = [item.to_dict() if hasattr(item, 'to_dict') else item for item in result['items']]
            self.cache.set('story', items)

        return result

    def update(self, id: int, data: Dict[str, Any]):
        if 'writerId' in data:
            data['writer_id'] = data.pop('writerId')

        self._validate(data)

        existing = self.repository.find_by_id(id)
        if not existing:
            raise NotFoundError(f"Story with id {id} not found")

        data['modified'] = datetime.utcnow()
        result = self.repository.update(id, data)

        if self.cache:
            self.cache.invalidate('story', id=id)

        return result

    def delete(self, id: int):
        if not self.repository.delete_by_id(id):
            raise NotFoundError(f"Story with id {id} not found")

        if self.cache:
            self.cache.invalidate('story', id=id)

    def _validate(self, data):
        if 'title' in data:
            if len(data['title']) < 2 or len(data['title']) > 64:
                raise ValidationError("Title must be between 2 and 64 characters", "40002")
        if 'content' in data:
            if len(data['content']) < 4 or len(data['content']) > 2048:
                raise ValidationError("Content must be between 4 and 2048 characters", "40003")