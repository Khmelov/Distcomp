from Task350.publisher.src.domain.models import Creator, Post, Marker
from Task350.publisher.src.domain.repositories.in_memory.in_memory_tweet import InMemoryTweetRepository
from Task350.publisher.src.domain.repositories.interfaces import InMemoryRepository

class InMemoryCreatorRepository(InMemoryRepository[Creator]):
    pass

class InMemoryPostRepository(InMemoryRepository[Post]):
    pass

class InMemoryMarkerRepository(InMemoryRepository[Marker]):
    pass