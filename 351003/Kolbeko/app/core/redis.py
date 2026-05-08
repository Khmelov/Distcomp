import os
import json
import redis.asyncio as redis
from typing import Any, Optional
import logging

REDIS_URL = os.getenv("REDIS_URL", "redis://localhost:6379/0")
redis_client: Optional[redis.Redis] = None

async def init_redis():
    global redis_client
    try:
        redis_client = redis.from_url(REDIS_URL, decode_responses=True)
        await redis_client.ping()
    except Exception as e:
        logging.error(f"Redis connection failed: {e}")
        redis_client = None

async def close_redis():
    global redis_client
    if redis_client:
        await redis_client.aclose()

async def set_cache(key: str, value: Any, expire: int = 3600):
    if redis_client is None: return
    try:
        serialized_data = json.dumps(value, default=str)
        await redis_client.set(key, serialized_data, ex=expire)
    except Exception as e:
        print(f"Redis SET error: {e}")

async def get_cache(key: str) -> Optional[Any]:
    if redis_client is None: return None
    try:
        data = await redis_client.get(key)
        if not data: return None
        return json.loads(data)
    except Exception as e:
        print(f"Redis GET error: {e}")
        return None

async def delete_cache(key: str):
    if not redis_client: return
    try:
        await redis_client.delete(key)
    except Exception:
        pass