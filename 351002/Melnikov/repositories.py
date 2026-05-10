from typing import TypeVar, Generic, Dict, List, Optional
from database import authors_db, issues_db, tags_db, comments_db

T = TypeVar('T')

class InMemoryRepository(Generic[T]):
    def __init__(self, db_store: Dict[int, T]):
        self.store = db_store

    def get_next_id(self) -> int:
        return max(self.store.keys(), default=0) + 1

    def save(self, entity: T) -> T:
        if getattr(entity, 'id', None) is None:
            entity.id = self.get_next_id()
        self.store[entity.id] = entity
        return entity

    def find_by_id(self, item_id: int) -> Optional[T]:
        return self.store.get(item_id)

    def find_all(self) -> List[T]:
        return list(self.store.values())

    def update(self, entity: T) -> T:
        self.store[entity.id] = entity
        return entity

    def delete(self, item_id: int) -> bool:
        if item_id in self.store:
            del self.store[item_id]
            return True
        return False

author_repo = InMemoryRepository(authors_db)
issue_repo = InMemoryRepository(issues_db)
tag_repo = InMemoryRepository(tags_db)
comment_repo = InMemoryRepository(comments_db)