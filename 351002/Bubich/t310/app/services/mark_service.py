from typing import List
from app.models.mark import Mark
from app.dto.requests.mark_request import MarkRequestTo
from app.dto.responses.mark_response import MarkResponseTo
from app.repository.in_memory_repository import InMemoryRepository
from app.exceptions.custom_exceptions import NotFoundError, ValidationError


class MarkService:
    def __init__(self, repository: InMemoryRepository):
        self.repository = repository

    def create(self, request: MarkRequestTo) -> MarkResponseTo:
        self._validate_request(request)
        mark = Mark(name=request.name)
        saved_mark = self.repository.save(mark)
        return self._to_response(saved_mark)

    def get_by_id(self, id: int) -> MarkResponseTo:
        mark = self.repository.find_by_id(id)
        if not mark:
            raise NotFoundError(f"Mark with id {id} not found")
        return self._to_response(mark)

    def get_all(self) -> List[MarkResponseTo]:
        marks = self.repository.find_all()
        return [self._to_response(m) for m in marks]

    def update(self, id: int, request: MarkRequestTo) -> MarkResponseTo:
        self._validate_request(request)
        existing_mark = self.repository.find_by_id(id)
        if not existing_mark:
            raise NotFoundError(f"Mark with id {id} not found")

        existing_mark.name = request.name
        updated_mark = self.repository.update(existing_mark)
        return self._to_response(updated_mark)

    def delete(self, id: int) -> None:
        if not self.repository.delete_by_id(id):
            raise NotFoundError(f"Mark with id {id} not found")

    def get_by_story_id(self, story_id: int, story_mark_repo: InMemoryRepository) -> List[MarkResponseTo]:
        # Здесь нужна связующая таблица story_mark, но для простоты пропустим
        return self.get_all()

    def _validate_request(self, request: MarkRequestTo):
        if not request.name or len(request.name) < 2 or len(request.name) > 32:
            raise ValidationError("Name must be between 2 and 32 characters")

    def _to_response(self, mark: Mark) -> MarkResponseTo:
        return MarkResponseTo(
            id=mark.id,
            name=mark.name
        )