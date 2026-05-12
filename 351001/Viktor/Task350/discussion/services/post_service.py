import httpx
import uuid
import asyncio
from typing import List, Dict, Any, Optional
from Task350.publisher.src.infrastructure.redis_client import RedisClient
from Task350.publisher.src.services.kafka_producer import KafkaProducerService
from Task350.publisher.src.services.kafka_consumer import KafkaConsumerService
from Task350.publisher.src.schemas.post import PostRequestTo

from ..infrastructure.models.post_model import PostState
from ..domain.repositories.post_repository import CassandraPostRepository

# Локальное исключение для discussion
class HttpNotFoundError(Exception):
    def __init__(self, message: str, error_code: int):
        self.message = message
        self.error_code = error_code
        super().__init__(message)

class PostService:
    def __init__(self, repo: CassandraPostRepository):
        self._repo = repo

    async def get_one(self, tweet_id: int, post_id: int):
        post = await self._repo.get_one(tweet_id, post_id)
        if not post:
            raise HttpNotFoundError("Post not found", 40401)
        return post

    async def get_all(self, tweet_id: int, page: int, size: int):
        return await self._repo.get_all(tweet_id, page, size)

    async def moderate(self, content: str) -> str:
        stop_words = ["spam", "badword"]
        if any(word in content.lower() for word in stop_words):
            return PostState.DECLINE
        return PostState.APPROVE

    async def create_post(self, tweet_id: int, content: str):
        state = await self.moderate(content)
        post = await self._repo.create(tweet_id, content, state)
        return post

    async def update_post(self, tweet_id: int, post_id: int, content: str):
        updated = await self._repo.update(tweet_id, post_id, content)
        if not updated:
            raise HttpNotFoundError("Post not found", 40401)
        return updated

    async def delete(self, tweet_id: int, post_id: int):
        await self._repo.delete(tweet_id, post_id)