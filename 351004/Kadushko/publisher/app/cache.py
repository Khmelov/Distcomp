import json
import redis

_client: redis.Redis | None = None


def get_redis() -> redis.Redis:
    global _client
    if _client is None:
        _client = redis.Redis(host="localhost", port=6379, db=0, decode_responses=True)
    return _client


def cache_get(key: str):
    try:
        val = get_redis().get(key)
        return json.loads(val) if val else None
    except Exception:
        return None


def cache_set(key: str, value, ttl: int = 60):
    try:
        get_redis().setex(key, ttl, json.dumps(value))
    except Exception:
        pass


def cache_delete(key: str):
    try:
        get_redis().delete(key)
    except Exception:
        pass


def cache_delete_pattern(pattern: str):
    try:
        r = get_redis()
        keys = r.keys(pattern)
        if keys:
            r.delete(*keys)
    except Exception:
        pass
