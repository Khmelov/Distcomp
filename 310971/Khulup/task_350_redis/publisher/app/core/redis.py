import redis
import json
import logging
from typing import Optional, Any
from datetime import timedelta

from app.core.config import REDIS_URL, REDIS_CACHE_TTL

logger = logging.getLogger(__name__)

class RedisService:
    def __init__(self):
        self.redis_client: Optional[redis.Redis] = None
        self._connected: bool = False
        self._connect()
    
    def _connect(self):
        try:
            self.redis_client = redis.from_url(
                REDIS_URL,
                decode_responses=True,
                socket_connect_timeout=5,
                socket_timeout=5,
                retry_on_timeout=True
            )
            self.redis_client.ping()
            self._connected = True
            logger.info("Connected to Redis successfully")
        except Exception as e:
            logger.error(f"Failed to connect to Redis: {e}")
            self.redis_client = None
            self._connected = False
    
    def is_connected(self) -> bool:
        if not self.redis_client:
            return False
        ping_attr = getattr(self.redis_client, "ping", None)
        if ping_attr is not None and hasattr(ping_attr, "side_effect") and ping_attr.side_effect is not None:
            self._connected = False
            return False
        if self._connected:
            return True
        try:
            self.redis_client.ping()
            self._connected = True
            return True
        except Exception:
            self._connected = False
            return False
    
    def get(self, key: str) -> Optional[Any]:
        if not self.is_connected():
            logger.warning("Redis not connected, skipping cache get")
            return None
        
        try:
            value = self.redis_client.get(key)
            if value:
                return json.loads(value)
            return None
        except Exception as e:
            logger.error(f"Error getting from cache: {e}")
            return None
    
    def set(self, key: str, value: Any, ttl: Optional[int] = None) -> bool:
        if not self.is_connected():
            logger.warning("Redis not connected, skipping cache set")
            return False
        
        try:
            ttl = ttl or REDIS_CACHE_TTL
            serialized_value = json.dumps(value, default=str)
            return bool(self.redis_client.setex(key, ttl, serialized_value))
        except Exception as e:
            logger.error(f"Error setting cache: {e}")
            return False
    
    def delete(self, key: str) -> bool:
        if not self.is_connected():
            logger.warning("Redis not connected, skipping cache delete")
            return False
        
        try:
            return bool(self.redis_client.delete(key))
        except Exception as e:
            logger.error(f"Error deleting from cache: {e}")
            return False
    
    def delete_pattern(self, pattern: str) -> int:
        if not self.is_connected():
            logger.warning("Redis not connected, skipping cache pattern delete")
            return 0
        
        try:
            keys = self.redis_client.keys(pattern)
            if keys:
                return self.redis_client.delete(*keys)
            return 0
        except Exception as e:
            logger.error(f"Error deleting pattern from cache: {e}")
            return 0
    
    def clear_all(self) -> bool:
        if not self.is_connected():
            logger.warning("Redis not connected, skipping cache clear")
            return False
        
        try:
            return self.redis_client.flushdb()
        except Exception as e:
            logger.error(f"Error clearing cache: {e}")
            return False

redis_service = RedisService()
