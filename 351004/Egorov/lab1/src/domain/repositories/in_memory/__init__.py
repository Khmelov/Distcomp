from src.domain.models import Author, Note, Tag
from src.domain.repositories.in_memory.in_memory_topic import InMemoryTopicRepository
from src.domain.repositories.interfaces import InMemoryRepository


class InMemoryAuthorRepository(InMemoryRepository[Author]):
    pass

class InMemoryNoteRepository(InMemoryRepository[Note]):
    pass

class InMemoryTagRepository(InMemoryRepository[Tag]):
    pass