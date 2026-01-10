import pytest
from unittest.mock import Mock, patch
from app.core.cache_decorators import cache_result, invalidate_cache_pattern


class TestCacheDecorators:
    
    @pytest.fixture
    def mock_redis_service(self):
        with patch('app.core.cache_decorators.redis_service') as mock:
            yield mock
    
    def test_cache_result_hit(self, mock_redis_service):
        test_data = {"id": 1, "name": "test"}
        mock_redis_service.get.return_value = test_data
        
        @cache_result("test", ttl=3600)
        def test_function(arg1, arg2=None):
            return {"result": "computed"}
        
        result = test_function(1, arg2="test")
        
        assert result == test_data
        mock_redis_service.get.assert_called_once()
        mock_redis_service.set.assert_not_called()
    
    def test_cache_result_miss(self, mock_redis_service):
        mock_redis_service.get.return_value = None
        computed_data = {"result": "computed"}
        
        @cache_result("test", ttl=3600)
        def test_function(arg1, arg2=None):
            return computed_data
        
        result = test_function(1, arg2="test")
        
        assert result == computed_data
        mock_redis_service.get.assert_called_once()
        mock_redis_service.set.assert_called_once()
    
    def test_cache_result_none_result(self, mock_redis_service):
        mock_redis_service.get.return_value = None
        
        @cache_result("test", ttl=3600)
        def test_function():
            return None
        
        result = test_function()
        
        assert result is None
        mock_redis_service.get.assert_called_once()
        mock_redis_service.set.assert_not_called()
    
    def test_cache_result_key_generation(self, mock_redis_service):
        mock_redis_service.get.return_value = None
        
        @cache_result("test", ttl=3600)
        def test_function(arg1, arg2, kwarg1=None):
            return {"result": "computed"}
        
        test_function(1, "test", kwarg1="value")
        
        expected_key_pattern = "test:(1, 'test'):[('kwarg1', 'value')]"
        mock_redis_service.get.assert_called_once()
        call_args = mock_redis_service.get.call_args[0][0]
        assert "test:" in call_args
        assert "1" in call_args
        assert "test" in call_args
        assert "kwarg1" in call_args
        assert "value" in call_args
    
    def test_invalidate_cache_pattern_success(self, mock_redis_service):
        mock_redis_service.delete_pattern.return_value = 3
        
        @invalidate_cache_pattern("test")
        def test_function():
            return {"result": "success"}
        
        result = test_function()
        
        assert result == {"result": "success"}
        mock_redis_service.delete_pattern.assert_called_once_with("test:*")
    
    def test_invalidate_cache_pattern_no_deletion(self, mock_redis_service):
        mock_redis_service.delete_pattern.return_value = 0
        
        @invalidate_cache_pattern("test")
        def test_function():
            return {"result": "success"}
        
        result = test_function()
        
        assert result == {"result": "success"}
        mock_redis_service.delete_pattern.assert_called_once_with("test:*")
    
    def test_invalidate_cache_pattern_with_exception(self, mock_redis_service):
        mock_redis_service.delete_pattern.side_effect = Exception("Redis error")
        
        @invalidate_cache_pattern("test")
        def test_function():
            return {"result": "success"}
        
        result = test_function()
        
        assert result == {"result": "success"}
        mock_redis_service.delete_pattern.assert_called_once_with("test:*")
