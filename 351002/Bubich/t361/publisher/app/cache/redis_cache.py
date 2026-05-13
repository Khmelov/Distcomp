import json
import redis
from typing import Optional, Union, Dict, List, Any
from app.config import Config


class RedisCache:
    """Redis кэш для publisher (опциональный)"""

    def __init__(self):
        self.redis = None
        self.ttl = Config.REDIS_TTL
        self._init_redis()

    def _init_redis(self):
        """Инициализация Redis с быстрым таймаутом"""
        try:
            self.redis = redis.Redis(
                host=Config.REDIS_HOST,
                port=Config.REDIS_PORT,
                db=Config.REDIS_DB,
                socket_connect_timeout=1,  # Быстрый таймаут
                socket_timeout=1,
                decode_responses=True
            )
            # Пробуем ping с таймаутом
            self.redis.ping()
            print("Redis connected successfully")
        except (redis.ConnectionError, redis.TimeoutError, Exception):
            print("Warning: Redis not available at {}:{}. Caching disabled.".format(
                Config.REDIS_HOST, Config.REDIS_PORT))
            self.redis = None

    def _make_key(self, entity: str, id: int = None, **params) -> str:
        """Создание ключа кэша"""
        if id:
            return f"{entity}:{id}"
        if params:
            param_str = ':'.join(f"{k}={v}" for k, v in sorted(params.items()) if v)
            return f"{entity}:list:{param_str}" if param_str else f"{entity}:all"
        return f"{entity}:all"

    def get(self, entity: str, id: int = None, **params) -> Optional[Union[Dict, List, Any]]:
        """Получение из кэша"""
        if not self.redis:
            return None

        key = self._make_key(entity, id, **params)
        try:
            data = self.redis.get(key)
            if data:
                return json.loads(data)
        except Exception:
            pass
        return None

    def set(self, entity: str, data: Any, id: int = None, **params) -> None:
        """Сохранение в кэш"""
        if not self.redis:
            return

        key = self._make_key(entity, id, **params)
        try:
            self.redis.setex(key, self.ttl, json.dumps(data, ensure_ascii=False))
        except Exception:
            pass

    def invalidate(self, entity: str, id: int = None) -> None:
        """Инвалидация кэша для сущности"""
        if not self.redis:
            return

        try:
            if id:
                self.redis.delete(f"{entity}:{id}")

            # Удаляем все списки этой сущности
            pattern = f"{entity}:*"
            keys = self.redis.keys(pattern)
            if keys:
                self.redis.delete(*keys)
        except Exception:
            pass

    def is_available(self) -> bool:
        """Проверка доступности Redis"""
        if not self.redis:
            return False
        try:
            self.redis.ping()
            return True
        except Exception:
            return False