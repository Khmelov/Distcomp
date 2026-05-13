import httpx
import uuid
import asyncio
from typing import List, Dict, Any, Optional
from Task350.publisher.src.infrastructure.redis_client import RedisClient
from Task350.publisher.src.services.kafka_producer import KafkaProducerService
from Task350.publisher.src.services.kafka_consumer import KafkaConsumerService
from Task350.publisher.src.schemas.post import PostRequestTo

class PostService:
    def __init__(self, producer: KafkaProducerService, consumer: KafkaConsumerService, redis: RedisClient):
        self.producer = producer
        self.consumer = consumer
        self.redis = redis
        self.pending_requests = {}
        self.base_url = "http://localhost:24130/api/v1.0"

    async def _send_and_wait(self, operation: str, data: dict) -> dict:
        corr_id = str(uuid.uuid4())
        future = asyncio.Future()
        self.pending_requests[corr_id] = future
        message = {"correlation_id": corr_id, "operation": operation, "data": data}
        key = str(data.get("tweet_id", 0))
        await self.producer.send_request("InTopic", key=key, value=message)
        try:
            result = await asyncio.wait_for(future, timeout=10.0)
            return result.get("result", {})
        except asyncio.TimeoutError:
            raise Exception("Timeout waiting for moderation")
        finally:
            self.pending_requests.pop(corr_id, None)

    async def create_post(self, tweet_id: int, content: str) -> Dict[str, Any]:
        result = await self._send_and_wait("create", {"tweet_id": tweet_id, "content": content})
        await self.redis.delete_pattern(f"posts_by_tweet:{tweet_id}:*")
        return result

    async def update_post(self, tweet_id: int, post_id: int, content: str) -> Dict[str, Any]:
        result = await self._send_and_wait("update", {"tweet_id": tweet_id, "post_id": post_id, "content": content})
        await self.redis.delete(f"post:{tweet_id}:{post_id}")
        await self.redis.delete_pattern(f"posts_by_tweet:{tweet_id}:*")
        return result

    async def delete_post(self, tweet_id: int, post_id: int) -> None:
        async with httpx.AsyncClient() as client:
            resp = await client.delete(f"{self.base_url}/posts/{tweet_id}/{post_id}")
            resp.raise_for_status()
        await self.redis.delete(f"post:{tweet_id}:{post_id}")
        await self.redis.delete_pattern(f"posts_by_tweet:{tweet_id}:*")

    async def get_posts_by_tweet(self, tweet_id: int, page: int = 1, size: int = 20) -> List[Dict[str, Any]]:
        cache_key = f"posts_by_tweet:{tweet_id}:page:{page}:size:{size}"
        cached = await self.redis.get(cache_key)
        if cached:
            return cached
        async with httpx.AsyncClient() as client:
            resp = await client.get(f"{self.base_url}/posts/by_tweet/{tweet_id}", params={"page": page, "size": size})
            resp.raise_for_status()
            data = resp.json()
            await self.redis.set(cache_key, data, ttl=30)
            return data

    async def get_post(self, tweet_id: int, post_id: int) -> Dict[str, Any]:
        cache_key = f"post:{tweet_id}:{post_id}"
        cached = await self.redis.get(cache_key)
        if cached:
            return cached
        async with httpx.AsyncClient() as client:
            resp = await client.get(f"{self.base_url}/posts/{tweet_id}/{post_id}")
            resp.raise_for_status()
            data = resp.json()
            await self.redis.set(cache_key, data, ttl=60)
            return data

    # Методы для совместимости с REST-контроллерами
    async def get_all(self, page: int = 1, size: int = 20, sort: str = "id", content: Optional[str] = None, tweet_id: Optional[int] = None) -> List[Dict[str, Any]]:
        if tweet_id is not None:
            return await self.get_posts_by_tweet(tweet_id, page, size)
        return []

    async def get_one(self, post_id: str) -> Dict[str, Any]:
        async with httpx.AsyncClient() as client:
            resp = await client.get(f"{self.base_url}/posts/{post_id}")
            resp.raise_for_status()
            return resp.json()

    async def create(self, dto: PostRequestTo) -> Dict[str, Any]:
        return await self.create_post(dto.tweet_id, dto.content)

    async def update(self, post_id: str, dto: PostRequestTo) -> Dict[str, Any]:
        return await self.update_post(dto.tweet_id, int(post_id), dto.content)

    async def delete(self, post_id: str) -> None:
        post = await self.get_one(post_id)
        tweet_id = post.get("tweet_id")
        if not tweet_id:
            raise ValueError("Post not found")
        await self.delete_post(tweet_id, int(post_id))