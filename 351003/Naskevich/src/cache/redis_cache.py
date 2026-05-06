import json
from typing import Any

from redis.asyncio import Redis


class RedisCache:
    def __init__(self, client: Redis) -> None:
        self._client = client

    async def get_json(self, key: str) -> Any | None:
        raw = await self._client.get(key)
        if raw is None:
            return None
        if isinstance(raw, bytes):
            raw = raw.decode()
        return json.loads(raw)

    async def set_json(self, key: str, value: Any, *, ttl_seconds: int | None) -> None:
        payload = json.dumps(value, ensure_ascii=False, default=str)
        if ttl_seconds is not None and ttl_seconds > 0:
            await self._client.set(key, payload, ex=ttl_seconds)
        else:
            await self._client.set(key, payload)

    async def delete(self, *keys: str) -> None:
        if not keys:
            return
        await self._client.delete(*keys)
