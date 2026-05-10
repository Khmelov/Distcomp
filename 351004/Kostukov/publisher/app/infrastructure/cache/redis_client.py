import redis.asyncio as redis
from typing import Optional


class RedisClient:
    def __init__(self, url: str = "redis://localhost:6379"):
        self.redis = redis.from_url(url, decode_responses=True)

    async def get(self, key: str) -> Optional[str]:
        return await self.redis.get(key)

    async def set(self, key: str, value: str, ttl: int = 120):
        await self.redis.set(key, value, ex=ttl)

    async def delete(self, key: str):
        await self.redis.delete(key)

    async def delete_pattern(self, pattern: str):
        async for key in self.redis.scan_iter(match=pattern):
            await self.redis.delete(key)

    async def close(self):
        await self.redis.close()