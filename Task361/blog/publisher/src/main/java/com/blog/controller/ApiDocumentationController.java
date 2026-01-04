package com.blog.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiDocumentationController {

    @GetMapping("/versions")
    public ResponseEntity<Map<String, Object>> getApiVersions() {
        Map<String, Object> response = new HashMap<>();

        Map<String, String> v1 = new HashMap<>();
        v1.put("version", "v1.0");
        v1.put("security", "None");
        v1.put("status", "Active");
        v1.put("baseUrl", "/api/v1.0");
        v1.put("description", "Legacy unprotected API");

        Map<String, String> v2 = new HashMap<>();
        v2.put("version", "v2.0");
        v2.put("security", "JWT Token Required");
        v2.put("status", "Active");
        v2.put("baseUrl", "/api/v2.0");
        v2.put("description", "Modern protected API with role-based access");

        response.put("v1.0", v1);
        response.put("v2.0", v2);
        response.put("currentActive", "Both v1.0 and v2.0");
        response.put("note", "v1.0 endpoints work without authentication, v2.0 requires JWT");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Blog Publisher API");
        response.put("port", "24110");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }
}