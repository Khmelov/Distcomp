import json
import logging
import redis
from app.core.config import settings

logger = logging.getLogger(__name__)

_client: redis.Redis | None = None


def get_redis() -> redis.Redis:
    global _client
    if _client is None:
        _client = redis.Redis(
            host=settings.REDIS_HOST,
            port=settings.REDIS_PORT,
            db=settings.REDIS_DB,
            decode_responses=True,
            socket_connect_timeout=2,
        )
    return _client


def cache_get(key: str):
    try:
        raw = get_redis().get(key)
        return json.loads(raw) if raw else None
    except Exception as e:
        logger.warning(f"Redis cache_get failed for key={key}: {e}")
        return None


def cache_set(key: str, value, ttl: int = 3600):
    try:
        get_redis().setex(key, ttl, json.dumps(value))
    except Exception as e:
        logger.warning(f"Redis cache_set failed for key={key}: {e}")


def cache_delete(key: str):
    try:
        get_redis().delete(key)
    except Exception as e:
        logger.warning(f"Redis cache_delete failed for key={key}: {e}")
