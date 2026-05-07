from functools import lru_cache

from src.domain.repositories.in_memory import InMemoryAuthorRepository, InMemoryNoteRepository, \
    InMemoryTopicRepository
from src.services import AuthorService, TopicService, NoteService, TagService


@lru_cache
def get_author_repo() -> InMemoryAuthorRepository:
    return InMemoryAuthorRepository()

def get_author_service() -> AuthorService:
    repo = get_author_repo()
    return AuthorService(repo)

@lru_cache
def get_topic_repo() -> InMemoryTopicRepository:
    return InMemoryTopicRepository()

def get_topic_service() -> TopicService:
    repo = get_topic_repo()
    return TopicService(repo)

@lru_cache
def get_note_repo() -> InMemoryNoteRepository:
    return InMemoryNoteRepository()

def get_note_service() -> NoteService:
    repo = get_note_repo()
    return NoteService(repo)

@lru_cache
def get_tag_repo() -> InMemoryTopicRepository:
    return InMemoryTopicRepository()

def get_tag_service() -> TagService:
    repo = get_tag_repo()
    return TagService(repo)