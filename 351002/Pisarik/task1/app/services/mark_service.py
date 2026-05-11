from typing import List, Optional
from app.models.mark import Mark
from app.repositories.in_memory_repository import InMemoryRepository
from app.dtos.mark_request import MarkRequestTo
from app.dtos.mark_response import MarkResponseTo

class MarkService:
    def __init__(self, repo: InMemoryRepository[Mark]) -> None:
        self._repo = repo

    def create_mark(self, dto: MarkRequestTo) -> MarkResponseTo:
        entity = Mark(id=self._repo.next_id(), name=dto.name)
        created = self._repo.create(entity)
        return MarkResponseTo(id=created.id, name=created.name)

    def get_mark(self, mark_id: int) -> Optional[MarkResponseTo]:
        entity = self._repo.get_by_id(mark_id)
        if not entity:
            return None
        return MarkResponseTo(id=entity.id, name=entity.name)

    def get_all_marks(self) -> List[MarkResponseTo]:
        return [MarkResponseTo(id=i.id, name=i.name) for i in self._repo.get_all()]

    def update_mark(self, mark_id: int, dto: MarkRequestTo) -> Optional[MarkResponseTo]:
        existing = self._repo.get_by_id(mark_id)
        if not existing:
            return None
        updated_entity = Mark(id=mark_id, name=dto.name)
        updated = self._repo.update(mark_id, updated_entity)
        if not updated:
            return None
        return MarkResponseTo(id=updated.id, name=updated.name)

    def delete_mark(self, mark_id: int) -> bool:
        return self._repo.delete(mark_id)
