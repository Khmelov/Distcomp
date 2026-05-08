from copy import deepcopy
from typing import Any, Generic, TypeVar

from app.repositories.base import CrudRepository
from app.repositories.paging import PageRequest, PageResult

T = TypeVar("T")


class InMemoryRepository(CrudRepository[T], Generic[T]):
    def __init__(self) -> None:
        self._items: dict[int, T] = {}
        self._next_id = 1

    def find_page(self, request: PageRequest, filters: dict[str, Any] | None) -> PageResult[T]:
        items = [deepcopy(x) for x in self._items.values()]
        if filters:
            for key, value in filters.items():
                items = [x for x in items if getattr(x, key, None) == value]
        for field, ascending in request.sort:
            items.sort(key=lambda e: getattr(e, field, ""), reverse=not ascending)
        total = len(items)
        start = request.page * request.size
        end = start + request.size
        return PageResult(items=items[start:end], total=total)

    def create(self, entity: T) -> T:
        entity.id = self._next_id
        self._next_id += 1
        self._items[entity.id] = deepcopy(entity)
        return deepcopy(entity)

    def find_by_id(self, entity_id: int) -> T | None:
        entity = self._items.get(entity_id)
        return deepcopy(entity) if entity is not None else None

    def update(self, entity: T) -> T | None:
        if entity.id not in self._items:
            return None
        self._items[entity.id] = deepcopy(entity)
        return deepcopy(entity)

    def delete_by_id(self, entity_id: int) -> bool:
        return self._items.pop(entity_id, None) is not None
