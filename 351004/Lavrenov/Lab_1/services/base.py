from typing import List, Optional, TypeVar, Generic
from repository.interface import IRepository

T = TypeVar("T")
R = TypeVar("R")  # Request DTO
S = TypeVar("S")  # Response DTO


class BaseService(Generic[T, R, S]):
    def __init__(self, repo: IRepository[T]):
        self._repo = repo

    def get(self, id: int) -> Optional[S]:
        entity = self._repo.get(id)
        return self._to_response(entity) if entity else None

    def get_all(self) -> List[S]:
        return [self._to_response(e) for e in self._repo.get_all()]

    def create(self, request: R) -> S:
        entity = self._to_entity(request)
        created = self._repo.create(entity)
        return self._to_response(created)

    def update(self, id: int, request: R) -> S:
        existing = self._repo.get(id)
        if not existing:
            raise ValueError("Entity not found")
        entity = self._to_entity(request)
        entity.id = id
        updated = self._repo.update(entity)
        return self._to_response(updated)

    def delete(self, id: int) -> bool:
        return self._repo.delete(id)

    def _to_entity(self, request: R) -> T:
        raise NotImplementedError

    def _to_response(self, entity: T) -> S:
        raise NotImplementedError
