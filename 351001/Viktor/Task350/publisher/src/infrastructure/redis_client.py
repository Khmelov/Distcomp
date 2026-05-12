import redis.asyncio as redis
import json
from typing import Optional, Any

class RedisClient:
    def __init__(self, host: str = 'localhost', port: int = 6379, db: int = 0):
        self.client = redis.Redis(host=host, port=port, db=db, decode_responses=True)

    async def get(self, key: str) -> Optional[Any]:
        """Получить значение по ключу (автоматически десериализует JSON)"""
        value = await self.client.get(key)
        if value:
            return json.loads(value)
        return None

    async def set(self, key: str, value: Any, ttl: int = 60):
        """Сохранить значение в Redis с TTL (секунды)"""
        await self.client.setex(key, ttl, json.dumps(value, default=str))

    async def delete(self, key: str):
        """Удалить ключ из Redis"""
        await self.client.delete(key)

    async def delete_pattern(self, pattern: str):
        """Удалить все ключи, соответствующие шаблону (например, 'creator:*')"""
        keys = await self.client.keys(pattern)
        if keys:
            await self.client.delete(*keys)