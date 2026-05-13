from typing import Dict, Any
from app.repository.database_repository import DatabaseRepository
from app.exceptions.custom_exceptions import NotFoundError, ValidationError
from app.cache.redis_cache import RedisCache


class MarkService:
    def __init__(self, repository: DatabaseRepository, cache: RedisCache = None):
        self.repository = repository
        self.cache = cache

    def create(self, data: Dict[str, Any]):
        self._validate(data)
        result = self.repository.save(data)
        if self.cache:
            self.cache.invalidate('mark')
        return result

    def get_by_id(self, id: int):
        if self.cache:
            cached = self.cache.get('mark', id=id)
            if cached:
                return type('MarkObj', (), {'to_dict': lambda: cached})()

        mark = self.repository.find_by_id(id)
        if not mark:
            raise NotFoundError(f"Mark with id {id} not found")

        if self.cache:
            self.cache.set('mark', mark.to_dict(), id=id)

        return mark

    def get_all(self):
        if self.cache:
            cached = self.cache.get('mark')
            if cached:
                items = [type('MarkObj', (), {'to_dict': lambda s=c: c})() for c in cached]
                return {'items': items, 'total': len(items)}

        result = self.repository.find_all()

        if self.cache and 'items' in result:
            items = [item.to_dict() if hasattr(item, 'to_dict') else item for item in result['items']]
            self.cache.set('mark', items)

        return result

    def update(self, id: int, data: Dict[str, Any]):
        self._validate(data)
        mark = self.repository.find_by_id(id)
        if not mark:
            raise NotFoundError(f"Mark with id {id} not found")
        result = self.repository.update(id, data)
        if self.cache:
            self.cache.invalidate('mark', id=id)
        return result

    def delete(self, id: int):
        if not self.repository.delete_by_id(id):
            raise NotFoundError(f"Mark with id {id} not found")
        if self.cache:
            self.cache.invalidate('mark', id=id)

    def _validate(self, data: Dict[str, Any]):
        if 'name' in data and (len(data['name']) < 2 or len(data['name']) > 32):
            raise ValidationError("Name must be between 2 and 32 characters", "40002")