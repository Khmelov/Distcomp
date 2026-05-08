import redis
import json

_client = None

def get_redis():
    global _client
    if _client is None:
        _client = redis.Redis(host='localhost', port=6379, db=0, decode_responses=True)
    return _client

CACHE_TTL = 60  # секунд

def cache_key(comment_id):
    return f"tbl_comment:{comment_id}"

def get_cached(comment_id):
    try:
        val = get_redis().get(cache_key(comment_id))
        return json.loads(val) if val else None
    except Exception:
        return None

def set_cached(comment):
    try:
        if comment and "id" in comment:
            get_redis().setex(cache_key(comment["id"]), CACHE_TTL, json.dumps(comment))
    except Exception:
        pass

def delete_cached(comment_id):
    try:
        get_redis().delete(cache_key(comment_id))
    except Exception:
        pass

def invalidate_all():
    try:
        keys = get_redis().keys("tbl_comment:*")
        if keys:
            get_redis().delete(*keys)
    except Exception:
        pass