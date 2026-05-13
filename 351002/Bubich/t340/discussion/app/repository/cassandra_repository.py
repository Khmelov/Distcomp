class CassandraRepository:
    def __init__(self):
        self._in_memory = {}
        self._next_id = 1
        self.session = None  # Cassandra недоступна — используем in-memory

    def init_schema(self):
        pass  # Не нужно для in-memory

    def save(self, comment):
        comment['id'] = self._next_id
        self._in_memory[self._next_id] = comment
        self._next_id += 1
        return comment

    def find_by_id(self, id):
        return self._in_memory.get(id)

    def find_by_story_id(self, story_id):
        return [c for c in self._in_memory.values() if c.get('story_id') == story_id]

    def find_all(self):
        return list(self._in_memory.values())

    def update(self, id, data):
        if id in self._in_memory:
            self._in_memory[id].update(data)
            return self._in_memory[id]
        return None

    def delete_by_id(self, id, story_id=None):
        if id in self._in_memory:
            del self._in_memory[id]
        return True