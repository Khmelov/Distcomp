from typing import TypeVar, Generic, Dict, List, Optional
from models import User, Issue, Label, Comment, IssueLabel

T = TypeVar('T')

class InMemoryRepository(Generic[T]):
    def __init__(self):
        self._storage: Dict[int, T] = {}
        self._id_counter = 1

    def save(self, entity) -> T:
        if not hasattr(entity, 'id') or entity.id is None:
            entity.id = self._id_counter
            self._id_counter += 1
        self._storage[entity.id] = entity
        return entity

    def find_by_id(self, id: int) -> Optional[T]:
        return self._storage.get(id)

    def find_all(self) -> List[T]:
        return list(self._storage.values())

    def update(self, id: int, entity) -> Optional[T]:
        if id in self._storage:
            self._storage[id] = entity
            return entity
        return None

    def delete(self, id: int) -> bool:
        if id in self._storage:
            del self._storage[id]
            return True
        return False

user_repo = InMemoryRepository[User]()
issue_repo = InMemoryRepository[Issue]()
label_repo = InMemoryRepository[Label]()
comment_repo = InMemoryRepository[Comment]()
issue_label_repo = InMemoryRepository[IssueLabel]()