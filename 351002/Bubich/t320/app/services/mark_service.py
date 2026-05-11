from typing import List, Dict, Any
from app.models.mark_model import MarkModel
from app.repository.database_repository import DatabaseRepository
from app.exceptions.custom_exceptions import NotFoundError, ValidationError


class MarkService:
    def __init__(self, repository: DatabaseRepository):
        self.repository = repository

    def create(self, data: Dict[str, Any]) -> MarkModel:
        self._validate(data)
        return self.repository.save(data)

    def get_by_id(self, id: int) -> MarkModel:
        mark = self.repository.find_by_id(id)
        if not mark:
            raise NotFoundError(f"Mark with id {id} not found")
        return mark

    def get_all(self):
        return self.repository.find_all()

    def update(self, id: int, data: Dict[str, Any]) -> MarkModel:
        self._validate(data)
        existing_mark = self.repository.find_by_id(id)
        if not existing_mark:
            raise NotFoundError(f"Mark with id {id} not found")
        return self.repository.update(id, data)

    def delete(self, id: int) -> None:
        if not self.repository.delete_by_id(id):
            raise NotFoundError(f"Mark with id {id} not found")

    def _validate(self, data: Dict[str, Any]):
        if 'name' in data and (len(data['name']) < 2 or len(data['name']) > 32):
            raise ValidationError("Name must be between 2 and 32 characters")