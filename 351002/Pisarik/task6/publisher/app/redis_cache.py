"""Redis cache-aside for publisher (Lab 5 / Task 350)."""

from __future__ import annotations

import hashlib
import json
import logging
import os
import threading
from typing import Any, Optional, Union

log = logging.getLogger(__name__)

_PREFIX = "distcomp:cache:v1"
_DEFAULT_URL = "redis://redis:6379/0"

_DISABLED = object()
_client: Union[Any, object, None] = None
_lock = threading.Lock()


def redis_url() -> str:
    return (os.getenv("REDIS_URL") or _DEFAULT_URL).strip()


def _connect() -> Optional[Any]:
    import redis as redis_lib

    url = redis_url()
    if not url or url.lower() in ("-", "none", "off"):
        return None
    try:
        r = redis_lib.from_url(url, decode_responses=True, socket_connect_timeout=3, socket_timeout=3)
        r.ping()
        return r
    except Exception as e:  # noqa: BLE001
        log.warning("Redis unavailable (%s); cache disabled", e)
        return None


def get_client():
    global _client
    with _lock:
        if _client is _DISABLED:
            return None
        if _client is None:
            c = _connect()
            _client = c if c is not None else _DISABLED
        return _client if _client is not _DISABLED else None


def cache_get_json(key: str) -> Optional[Any]:
    r = get_client()
    if not r:
        return None
    try:
        raw = r.get(key)
        if raw is None:
            return None
        return json.loads(raw)
    except Exception:  # noqa: BLE001
        return None


def cache_set_json(key: str, value: Any, ttl_sec: Optional[int] = None) -> None:
    r = get_client()
    if not r:
        return
    try:
        payload = json.dumps(value, ensure_ascii=False, default=str)
        if ttl_sec is not None:
            r.setex(key, ttl_sec, payload)
        else:
            r.set(key, payload)
    except Exception as e:  # noqa: BLE001
        log.debug("cache set failed: %s", e)


def cache_delete(key: str) -> None:
    r = get_client()
    if not r:
        return
    try:
        r.delete(key)
    except Exception:
        pass


def cache_delete_pattern(match: str) -> None:
    r = get_client()
    if not r:
        return
    try:
        for k in r.scan_iter(match=match, count=200):
            r.delete(k)
    except Exception as e:  # noqa: BLE001
        log.debug("cache delete pattern failed: %s", e)


def stable_hash(obj: dict[str, Any]) -> str:
    s = json.dumps(obj, sort_keys=True, ensure_ascii=False, default=str)
    return hashlib.sha256(s.encode("utf-8")).hexdigest()[:32]


# --- Message (Kafka / Cassandra path) ---


def message_id_key(message_id: int) -> str:
    return f"{_PREFIX}:message:id:{message_id}"


def message_list_key(payload: dict[str, Any]) -> str:
    return f"{_PREFIX}:message:list:{stable_hash(payload)}"


def invalidate_message_id(message_id: int) -> None:
    cache_delete(message_id_key(message_id))


def invalidate_all_message_lists() -> None:
    cache_delete_pattern(f"{_PREFIX}:message:list:*")


def invalidate_message_writes(message_id: Optional[int] = None) -> None:
    if message_id is not None:
        invalidate_message_id(message_id)
    invalidate_all_message_lists()


# --- Postgres entities ---


def entity_id_key(entity: str, eid: int) -> str:
    return f"{_PREFIX}:{entity}:id:{eid}"


def entity_list_key(entity: str, params: dict[str, Any]) -> str:
    return f"{_PREFIX}:{entity}:list:{stable_hash(params)}"


def invalidate_entity(entity: str, entity_id: Optional[int] = None) -> None:
    if entity_id is not None:
        cache_delete(entity_id_key(entity, entity_id))
    cache_delete_pattern(f"{_PREFIX}:{entity}:list:*")
