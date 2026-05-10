from .interface import NoteRepository
from .cassandra_repository import CassandraNoteRepository

__all__ = ["NoteRepository", "CassandraNoteRepository"]
