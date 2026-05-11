from typing import Dict, Any
from app.repository.database_repository import DatabaseRepository
from publisher.app.exceptions import NotFoundError, ValidationError


class WriterService:
    def __init__(self, repository: DatabaseRepository):
        self.repository = repository

    def create(self, data: Dict[str, Any]):
        self._validate_create(data)
        return self.repository.save(data)

    def get_by_id(self, id: int):
        writer = self.repository.find_by_id(id)
        if not writer:
            raise NotFoundError(f"Writer with id {id} not found")
        return writer

    def get_all(self):
        return self.repository.find_all()

    def update(self, id: int, data: Dict[str, Any]):
        self._validate_update(data)
        existing_writer = self.repository.find_by_id(id)
        if not existing_writer:
            raise NotFoundError(f"Writer with id {id} not found")

        update_data = {}
        if 'login' in data:
            update_data['login'] = data['login']
        if 'password' in data:
            update_data['password'] = data['password']
        if 'firstname' in data:
            update_data['firstname'] = data['firstname']
        if 'lastname' in data:
            update_data['lastname'] = data['lastname']

        return self.repository.update(id, update_data)

    def delete(self, id: int):
        """Удаление writer с проверкой существования"""
        writer = self.repository.find_by_id(id)
        if not writer:
            raise NotFoundError(f"Writer with id {id} not found")

        try:
            self.repository.delete_by_id(id)
        except Exception as e:
            raise Exception(f"Error deleting writer: {str(e)}")

    def _validate_create(self, data: Dict[str, Any]):
        if not data.get('login') or len(data['login']) < 2 or len(data['login']) > 64:
            raise ValidationError("Login must be between 2 and 64 characters", "40002")
        if not data.get('password') or len(data['password']) < 8 or len(data['password']) > 128:
            raise ValidationError("Password must be between 8 and 128 characters", "40003")
        if not data.get('firstname') or len(data['firstname']) < 2 or len(data['firstname']) > 64:
            raise ValidationError("Firstname must be between 2 and 64 characters", "40004")
        if not data.get('lastname') or len(data['lastname']) < 2 or len(data['lastname']) > 64:
            raise ValidationError("Lastname must be between 2 and 64 characters", "40005")

    def _validate_update(self, data: Dict[str, Any]):
        if 'login' in data and (len(data['login']) < 2 or len(data['login']) > 64):
            raise ValidationError("Login must be between 2 and 64 characters", "40002")
        if 'password' in data and (len(data['password']) < 8 or len(data['password']) > 128):
            raise ValidationError("Password must be between 8 and 128 characters", "40003")
        if 'firstname' in data and (len(data['firstname']) < 2 or len(data['firstname']) > 64):
            raise ValidationError("Firstname must be between 2 and 64 characters", "40004")
        if 'lastname' in data and (len(data['lastname']) < 2 or len(data['lastname']) > 64):
            raise ValidationError("Lastname must be between 2 and 64 characters", "40005")