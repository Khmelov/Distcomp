from typing import Dict, Any
from app.repository.database_repository import DatabaseRepository
from app.exceptions.custom_exceptions import NotFoundError, ValidationError, DuplicateError
from app.cache.redis_cache import RedisCache


class WriterService:
    def __init__(self, repository: DatabaseRepository, cache: RedisCache = None):
        self.repository = repository
        self.cache = cache

    def create(self, data: Dict[str, Any]):
        self._validate_create(data)
        result = self.repository.save(data)
        if self.cache:
            self.cache.invalidate('writer')
        return result

    def get_by_id(self, id: int):
        if self.cache:
            cached = self.cache.get('writer', id=id)
            if cached:
                # Возвращаем объект с to_dict или словарь
                return type('WriterObj', (), {'to_dict': lambda: cached, '__dict__': cached})()

        writer = self.repository.find_by_id(id)
        if not writer:
            raise NotFoundError(f"Writer with id {id} not found")

        if self.cache:
            self.cache.set('writer', writer.to_dict(), id=id)

        return writer

    def get_all(self):
        if self.cache:
            cached = self.cache.get('writer')
            if cached:
                items = [type('WriterObj', (), {'to_dict': lambda s=c: c})() for c in cached]
                return {'items': items, 'total': len(items)}

        result = self.repository.find_all()

        if self.cache and 'items' in result:
            items = [item.to_dict() if hasattr(item, 'to_dict') else item for item in result['items']]
            self.cache.set('writer', items)

        return result

    def update(self, id: int, data: Dict[str, Any]):
        self._validate_update(data)
        writer = self.repository.find_by_id(id)
        if not writer:
            raise NotFoundError(f"Writer with id {id} not found")
        result = self.repository.update(id, data)
        if self.cache:
            self.cache.invalidate('writer', id=id)
        return result

    def delete(self, id: int):
        if not self.repository.delete_by_id(id):
            raise NotFoundError(f"Writer with id {id} not found")
        if self.cache:
            self.cache.invalidate('writer', id=id)

    def _validate_create(self, data):
        if not data.get('login') or len(data['login']) < 2 or len(data['login']) > 64:
            raise ValidationError("Login must be between 2 and 64 characters", "40002")
        if not data.get('password') or len(data['password']) < 8 or len(data['password']) > 128:
            raise ValidationError("Password must be between 8 and 128 characters", "40003")
        if not data.get('firstname') or len(data['firstname']) < 2 or len(data['firstname']) > 64:
            raise ValidationError("Firstname must be between 2 and 64 characters", "40004")
        if not data.get('lastname') or len(data['lastname']) < 2 or len(data['lastname']) > 64:
            raise ValidationError("Lastname must be between 2 and 64 characters", "40005")

    def _validate_update(self, data):
        if 'login' in data and (len(data['login']) < 2 or len(data['login']) > 64):
            raise ValidationError("Login must be between 2 and 64 characters", "40002")
        if 'password' in data and (len(data['password']) < 8 or len(data['password']) > 128):
            raise ValidationError("Password must be between 8 and 128 characters", "40003")
        if 'firstname' in data and (len(data['firstname']) < 2 or len(data['firstname']) > 64):
            raise ValidationError("Firstname must be between 2 and 64 characters", "40004")
        if 'lastname' in data and (len(data['lastname']) < 2 or len(data['lastname']) > 64):
            raise ValidationError("Lastname must be between 2 and 64 characters", "40005")