class InMemoryRepository:
    def __init__(self):
        self._data = {}
        self._id = 1

    def create(self, entity):
        entity.id = self._id
        self._data[self._id] = entity
        self._id += 1
        return entity

    def find_by_id(self, id: int):
        return self._data.get(id)

    def find_all(self):
        return list(self._data.values())

    def update(self, id: int, entity):
        self._data[id] = entity
        return entity

    def delete(self, id: int):
        self._data.pop(id, None)
