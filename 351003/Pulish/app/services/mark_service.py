from app.dto.mark import MarkRequestTo, MarkResponseTo
from app.models.mark import Mark
from app.repository.inmemory import InMemoryRepository
from app.core.exceptions import NotFoundException


class MarkService:
    def __init__(self):
        self.repo = InMemoryRepository()

    def create(self, dto: MarkRequestTo) -> MarkResponseTo:
        mark = Mark(id=0, name=dto.name)
        saved = self.repo.create(mark)
        return self._to_response(saved)

    def find_all(self):
        return [self._to_response(m) for m in self.repo.find_all()]

    def find_by_id(self, id: int):
        mark = self.repo.find_by_id(id)
        if not mark:
            raise NotFoundException("Mark not found", 40403)
        return self._to_response(mark)

    def update(self, id: int, dto: MarkRequestTo):
        mark = self.repo.find_by_id(id)
        if not mark:
            raise NotFoundException("Mark not found", 40403)
        mark.name = dto.name
        return self._to_response(mark)

    def delete(self, id: int):
        mark = self.repo.find_by_id(id)
        if not mark:
            raise NotFoundException("Mark not found", 40401)
        self.repo.delete(id)

    def _to_response(self, mark: Mark) -> MarkResponseTo:
        return MarkResponseTo(id=mark.id, name=mark.name)
