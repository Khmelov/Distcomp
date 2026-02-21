from functools import lru_cache

from src.domain.repositories.in_memory import (
    InMemoryCreatorRepository,
    InMemoryTweetRepository,
    InMemoryPostRepository,
    InMemoryMarkerRepository,
)
from src.services import CreatorService, TweetService, PostService, MarkerService

@lru_cache
def get_creator_repo() -> InMemoryCreatorRepository:
    return InMemoryCreatorRepository()

def get_creator_service() -> CreatorService:
    return CreatorService(get_creator_repo())

@lru_cache
def get_tweet_repo() -> InMemoryTweetRepository:
    return InMemoryTweetRepository()

def get_tweet_service() -> TweetService:
    return TweetService(get_tweet_repo())

@lru_cache
def get_post_repo() -> InMemoryPostRepository:
    return InMemoryPostRepository()

def get_post_service() -> PostService:
    return PostService(get_post_repo())

@lru_cache
def get_marker_repo() -> InMemoryMarkerRepository:
    return InMemoryMarkerRepository()

def get_marker_service() -> MarkerService:
    return MarkerService(get_marker_repo())