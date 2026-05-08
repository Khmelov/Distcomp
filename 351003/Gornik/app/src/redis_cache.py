import json
import logging

import redis.asyncio as redis
from config.settings import get_settings

logger = logging.getLogger(__name__)

_redis: redis.Redis | None = None

TTL = 30


async def get_redis() -> redis.Redis:
    global _redis
    if _redis is None:
        settings = get_settings()
        _redis = redis.Redis(
            host=settings.redis_host,
            port=settings.redis_port,
            decode_responses=True,
        )
    return _redis


async def close_redis():
    global _redis
    if _redis:
        await _redis.close()
        _redis = None


async def cache_get(key: str) -> dict | list | None:
    try:
        r = await get_redis()
        data = await r.get(key)
        if data:
            return json.loads(data)
    except Exception:
        logger.debug("Redis cache miss/error for key=%s", key)
    return None


async def cache_set(key: str, value, ttl: int = TTL):
    try:
        r = await get_redis()
        await r.set(key, json.dumps(value, default=str), ex=ttl)
    except Exception:
        logger.debug("Redis cache set error for key=%s", key)


async def cache_delete(key: str):
    try:
        r = await get_redis()
        await r.delete(key)
    except Exception:
        logger.debug("Redis cache delete error for key=%s", key)


async def cache_delete_pattern(pattern: str):
    try:
        r = await get_redis()
        keys = []
        async for key in r.scan_iter(match=pattern):
            keys.append(key)
        if keys:
            await r.delete(*keys)
    except Exception:
        logger.debug("Redis cache delete pattern error for pattern=%s", pattern)
