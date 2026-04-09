from .in_memory_repository import InMemoryRepository
from ..models.author import Author
from ..models.issue import Issue
from ..models.tag import Tag
from ..models.comment import Comment

author_repository = InMemoryRepository[Author]()
issue_repository = InMemoryRepository[Issue]()
tag_repository = InMemoryRepository[Tag]()
comment_repository = InMemoryRepository[Comment]()