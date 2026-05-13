import os
import logging
import redis.asyncio as redis


REDIS_URL = os.getenv("REDIS_URL", "redis://localhost:6379/0")
redis_client = redis.from_url(REDIS_URL, decode_responses=True)


async def get_cache(key: str) -> str | None:
    try:
        return await redis_client.get(key)
    except Exception as e:
        logging.error(f"Redis GET Error: {e}")
        return None

async def set_cache(key: str, value: str, ex: int = 3600):
    try:
        await redis_client.set(key, value, ex=ex)
    except Exception as e:
        logging.error(f"Redis SET Error: {e}")


async def invalidate_cache_by_prefix(prefix: str):
    try:
        cursor = 0
        while True:
            cursor, keys = await redis_client.scan(cursor=cursor, match=f"{prefix}:*", count=100)
            if keys:
                await redis_client.delete(*keys)
            if cursor == 0:
                break
    except Exception as e:
        logging.error(f"Redis INVALIDATE Error: {e}")


async def close_redis():
    await redis_client.aclose()