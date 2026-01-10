import pytest
import json
from unittest.mock import Mock, patch
from app.core.redis import RedisService


class TestRedisService:
    
    @pytest.fixture
    def redis_service(self):
        with patch('redis.from_url') as mock_redis:
            mock_client = Mock()
            mock_redis.return_value = mock_client
            mock_client.ping.return_value = True
            
            service = RedisService()
            service.redis_client = mock_client
            return service
    
    def test_connect_success(self):
        with patch('redis.from_url') as mock_redis:
            mock_client = Mock()
            mock_redis.return_value = mock_client
            mock_client.ping.return_value = True
            
            service = RedisService()
            
            assert service.redis_client == mock_client
            mock_client.ping.assert_called_once()
    
    def test_connect_failure(self):
        with patch('redis.from_url') as mock_redis:
            mock_redis.side_effect = Exception("Connection failed")
            
            service = RedisService()
            
            assert service.redis_client is None
    
    def test_is_connected_true(self, redis_service):
        redis_service.redis_client.ping.return_value = True
        
        assert redis_service.is_connected() is True
        redis_service.redis_client.ping.assert_called_once()
    
    def test_is_connected_false(self, redis_service):
        redis_service.redis_client.ping.side_effect = Exception("No connection")
        
        assert redis_service.is_connected() is False
    
    def test_get_success(self, redis_service):
        test_data = {"id": 1, "name": "test"}
        redis_service.redis_client.get.return_value = json.dumps(test_data)
        
        result = redis_service.get("test_key")
        
        assert result == test_data
        redis_service.redis_client.get.assert_called_once_with("test_key")
    
    def test_get_not_found(self, redis_service):
        redis_service.redis_client.get.return_value = None
        
        result = redis_service.get("test_key")
        
        assert result is None
        redis_service.redis_client.get.assert_called_once_with("test_key")
    
    def test_get_not_connected(self, redis_service):
        redis_service.redis_client = None
        
        result = redis_service.get("test_key")
        
        assert result is None
    
    def test_set_success(self, redis_service):
        test_data = {"id": 1, "name": "test"}
        
        result = redis_service.set("test_key", test_data, 3600)
        
        assert result is True
        redis_service.redis_client.setex.assert_called_once()
    
    def test_set_not_connected(self, redis_service):
        redis_service.redis_client = None
        
        result = redis_service.set("test_key", {"test": "data"})
        
        assert result is False
    
    def test_delete_success(self, redis_service):
        redis_service.redis_client.delete.return_value = 1
        
        result = redis_service.delete("test_key")
        
        assert result is True
        redis_service.redis_client.delete.assert_called_once_with("test_key")
    
    def test_delete_not_found(self, redis_service):
        redis_service.redis_client.delete.return_value = 0
        
        result = redis_service.delete("test_key")
        
        assert result is False
    
    def test_delete_pattern_success(self, redis_service):
        redis_service.redis_client.keys.return_value = ["test_key1", "test_key2"]
        redis_service.redis_client.delete.return_value = 2
        
        result = redis_service.delete_pattern("test_*")
        
        assert result == 2
        redis_service.redis_client.keys.assert_called_once_with("test_*")
        redis_service.redis_client.delete.assert_called_once_with("test_key1", "test_key2")
    
    def test_delete_pattern_no_keys(self, redis_service):
        redis_service.redis_client.keys.return_value = []
        
        result = redis_service.delete_pattern("test_*")
        
        assert result == 0
        redis_service.redis_client.keys.assert_called_once_with("test_*")
        redis_service.redis_client.delete.assert_not_called()
    
    def test_clear_all_success(self, redis_service):
        redis_service.redis_client.flushdb.return_value = True
        
        result = redis_service.clear_all()
        
        assert result is True
        redis_service.redis_client.flushdb.assert_called_once()
