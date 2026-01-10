import functools
import inspect
import json
import logging
from typing import Any, Callable, Optional

from fastapi.encoders import jsonable_encoder

from app.core.redis import redis_service

logger = logging.getLogger(__name__)

def cache_result(key_prefix: str, ttl: Optional[int] = None):
    def decorator(func: Callable) -> Callable:
        def _make_cache_key(args, kwargs) -> str:
            call_args = args
            if call_args and hasattr(call_args[0], "__class__"):
                call_args = call_args[1:]

            try:
                payload = {
                    "args": call_args,
                    "kwargs": kwargs,
                }
                args_part = json.dumps(payload, sort_keys=True, default=str)
            except Exception:
                args_part = f"{str(call_args)}:{str(sorted(kwargs.items()))}"

            return f"{key_prefix}:{func.__name__}:{args_part}"

        def _is_cache_payload_ok(value: Any) -> bool:
            return value is None or isinstance(value, (dict, list, str, int, float, bool))

        if inspect.iscoroutinefunction(func):
            @functools.wraps(func)
            async def async_wrapper(*args, **kwargs) -> Any:
                cache_key = _make_cache_key(args, kwargs)

                cached_result = redis_service.get(cache_key)
                if cached_result is not None:
                    if not _is_cache_payload_ok(cached_result) or isinstance(cached_result, str):
                        redis_service.delete(cache_key)
                    else:
                        logger.debug(f"Cache hit for key: {cache_key}")
                        return cached_result
                    logger.debug(f"Cache hit for key: {cache_key}")

                result = await func(*args, **kwargs)
                if result is not None:
                    try:
                        encoded = jsonable_encoder(result)
                        redis_service.set(cache_key, encoded, ttl)
                        logger.debug(f"Cached result for key: {cache_key}")
                        return encoded
                    except Exception as e:
                        logger.warning(f"Cache encode/set failed for key {cache_key}: {e}")

                return result

            return async_wrapper

        @functools.wraps(func)
        def sync_wrapper(*args, **kwargs) -> Any:
            cache_key = _make_cache_key(args, kwargs)

            cached_result = redis_service.get(cache_key)
            if cached_result is not None:
                if not _is_cache_payload_ok(cached_result) or isinstance(cached_result, str):
                    redis_service.delete(cache_key)
                else:
                    logger.debug(f"Cache hit for key: {cache_key}")
                    return cached_result
                logger.debug(f"Cache hit for key: {cache_key}")

            result = func(*args, **kwargs)
            if result is not None:
                try:
                    encoded = jsonable_encoder(result)
                    redis_service.set(cache_key, encoded, ttl)
                    logger.debug(f"Cached result for key: {cache_key}")
                    return encoded
                except Exception as e:
                    logger.warning(f"Cache encode/set failed for key {cache_key}: {e}")

            return result

        return sync_wrapper
    return decorator

def invalidate_cache_pattern(key_pattern: str):
    def decorator(func: Callable) -> Callable:
        def _invalidate_related_patterns() -> int:
            try:
                return redis_service.delete_pattern(f"{key_pattern}:*")
            except Exception as e:
                logger.warning(f"Cache invalidation failed for pattern {key_pattern}: {e}")
                return 0

        if inspect.iscoroutinefunction(func):
            @functools.wraps(func)
            async def async_wrapper(*args, **kwargs) -> Any:
                result = await func(*args, **kwargs)

                deleted_count = _invalidate_related_patterns()
                if deleted_count > 0:
                    logger.debug(
                        f"Invalidated {deleted_count} cache entries matching pattern: {key_pattern}"
                    )

                return result

            return async_wrapper

        @functools.wraps(func)
        def sync_wrapper(*args, **kwargs) -> Any:
            result = func(*args, **kwargs)

            deleted_count = _invalidate_related_patterns()
            if deleted_count > 0:
                logger.debug(f"Invalidated {deleted_count} cache entries matching pattern: {key_pattern}")

            return result

        return sync_wrapper
    return decorator
