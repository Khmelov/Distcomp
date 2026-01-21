package com.example.publisher.controller;

import com.example.publisher.service.RedisCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1.0/cache")
public class CacheController {

    @Autowired
    private RedisCacheService cacheService;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("status", "active");
        stats.put("message", "Redis cache is running");
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/clear")
    public ResponseEntity<Map<String, String>> clearCache() {
        cacheService.clearAllCache();
        return ResponseEntity.ok(Map.of("message", "Cache cleared successfully"));
    }

    @DeleteMapping("/{pattern}")
    public ResponseEntity<Map<String, String>> clearCacheByPattern(@PathVariable String pattern) {
        cacheService.clearCacheByPattern(pattern);
        return ResponseEntity.ok(Map.of(
                "message", "Cache cleared for pattern: " + pattern
        ));
    }
}