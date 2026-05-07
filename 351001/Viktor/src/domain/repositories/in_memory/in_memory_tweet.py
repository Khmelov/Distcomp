from datetime import datetime
from src.domain.models import Tweet
from src.domain.repositories.interfaces import InMemoryRepository

class InMemoryTweetRepository(InMemoryRepository[Tweet]):
    def create(self, tweet: Tweet) -> Tweet:
        new_id = self._next_id()
        tweet.id = new_id
        tweet.created_at = datetime.now()
        tweet.updated_at = datetime.now()
        self._data[new_id] = tweet
        return tweet

    def update(self, tweet: Tweet) -> Tweet:
        tweet.created_at = self._data[tweet.id].created_at
        tweet.updated_at = datetime.now()
        self._data[tweet.id] = tweet
        return tweet