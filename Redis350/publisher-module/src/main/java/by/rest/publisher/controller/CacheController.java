package by.rest.publisher.controller;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired; 
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1.0/cache")
public class CacheController {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    public CacheController(@Autowired(required = false) RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        
        if (redisTemplate == null) {
            response.put("status", "DOWN");
            response.put("available", false);
            response.put("message", "Redis template is not configured");
            return ResponseEntity.ok(response);
        }
        
        try {
            // Простая проверка Redis
            String result = redisTemplate.getConnectionFactory()
                .getConnection()
                .ping();
            
            boolean isAvailable = "PONG".equals(result);
            
            response.put("status", isAvailable ? "UP" : "DOWN");
            response.put("available", isAvailable);
            response.put("message", isAvailable ? 
                "Redis cache is available" : 
                "Redis ping failed");
                
        } catch (Exception e) {
            response.put("status", "DOWN");
            response.put("available", false);
            response.put("message", "Redis cache is not available: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> response = new HashMap<>();
        
        if (redisTemplate == null) {
            response.put("status", "ERROR");
            response.put("message", "Redis is not configured");
            return ResponseEntity.ok(response);
        }
        
        try {
            Set<String> keys = redisTemplate.keys("publisher:*");
            int total = keys != null ? keys.size() : 0;
            
            int editors = 0, stories = 0, comments = 0, tags = 0;
            if (keys != null) {
                for (String key : keys) {
                    if (key.contains("editor:")) editors++;
                    else if (key.contains("story:")) stories++;
                    else if (key.contains("comment:")) comments++;
                    else if (key.contains("tag:")) tags++;
                }
            }
            
            response.put("status", "SUCCESS");
            response.put("total", total);
            
            Map<String, Integer> cachedItems = new HashMap<>();
            cachedItems.put("editors", editors);
            cachedItems.put("stories", stories);
            cachedItems.put("comments", comments);
            cachedItems.put("tags", tags);
            
            response.put("cachedItems", cachedItems);
            response.put("message", "Cache statistics retrieved successfully");
            
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Failed to get cache statistics: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testCache() {
        Map<String, Object> response = new HashMap<>();
        
        if (redisTemplate == null) {
            response.put("status", "ERROR");
            response.put("cacheWorking", false);
            response.put("message", "Redis is not configured");
            return ResponseEntity.ok(response);
        }
        
        try {
            String testKey = "publisher:test:key";
            String testValue = "test-value-" + System.currentTimeMillis();
            
            redisTemplate.opsForValue().set(testKey, testValue);
            String retrieved = (String) redisTemplate.opsForValue().get(testKey);
            
            boolean cacheWorking = retrieved != null && retrieved.equals(testValue);
            
            response.put("status", "SUCCESS");
            response.put("cacheWorking", cacheWorking);
            response.put("message", "Cache test completed successfully");
            
            redisTemplate.delete(testKey);
            
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("cacheWorking", false);
            response.put("message", "Cache test failed: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/clear")
    public ResponseEntity<Map<String, Object>> clearCache() {
        Map<String, Object> response = new HashMap<>();
        
        if (redisTemplate == null) {
            response.put("status", "ERROR");
            response.put("message", "Redis is not configured");
            return ResponseEntity.ok(response);
        }
        
        try {
            Set<String> keys = redisTemplate.keys("publisher:*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
            response.put("status", "SUCCESS");
            response.put("message", "Cache cleared successfully");
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Failed to clear cache: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
}