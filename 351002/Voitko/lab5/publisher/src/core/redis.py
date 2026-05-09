from redis.asyncio import Redis, from_url

from .settings import settings

redis_client: Redis | None = None

def init_redis():
    global redis_client
    redis_client = from_url(settings.redis.get_url, decode_responses=True)

async def close_redis():
    global redis_client

    if redis_client:
        await redis_client.aclose()
        redis_client = None

def get_redis() -> Redis:
    return redis_client