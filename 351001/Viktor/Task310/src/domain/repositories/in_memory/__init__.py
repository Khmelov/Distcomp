from Task310.src.domain.models import Creator, Tweet, Post, Marker
from Task310.src.domain.repositories.in_memory.in_memory_tweet import InMemoryTweetRepository
from Task310.src.domain.repositories.interfaces import InMemoryRepository

class InMemoryCreatorRepository(InMemoryRepository[Creator]):
    pass

class InMemoryPostRepository(InMemoryRepository[Post]):
    pass

class InMemoryMarkerRepository(InMemoryRepository[Marker]):
    pass