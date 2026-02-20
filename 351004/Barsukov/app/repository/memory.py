from typing import Dict, List, Optional


class InMemoryRepository:
    def __init__(self):
        self._data: Dict[int, dict] = {}
        self._counter = 1

    def save(self, item: dict) -> dict:
        obj_id = item.get("id")
        if obj_id is None:
            obj_id = self._counter
            item["id"] = obj_id
            self._counter += 1
        self._data[obj_id] = item
        return item

    def find_all(self) -> List[dict]:
        return list(self._data.values())

    def find_by_id(self, id: int) -> Optional[dict]:
        return self._data.get(id)

    def delete(self, id: int) -> bool:
        return self._data.pop(id, None) is not None


author_repo = InMemoryRepository()
issue_repo = InMemoryRepository()
sticker_repo = InMemoryRepository()
note_repo = InMemoryRepository()

author_repo.save({
    "id": 1,
    "login": "egorbarsukov3@gmail.com",
    "password": "password123",
    "firstname": "Егор",
    "lastname": "Барсуков",
})
author_repo._counter = 2
