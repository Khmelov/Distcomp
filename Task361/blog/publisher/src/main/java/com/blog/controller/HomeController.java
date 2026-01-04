package com.blog.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<Map<String, String>> home() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Blog API is running");
        response.put("version", "v1.0 & v2.0");
        response.put("status", "OK");
        response.put("port", "24110");
        response.put("documentation", "Visit /api/versions for API information");
        response.put("health", "Visit /api/health for service status");
        response.put("security", "v1.0 - no auth, v2.0 - JWT auth");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/v1.0")
    public ResponseEntity<Map<String, String>> apiInfoV1() {
        Map<String, String> response = new HashMap<>();
        response.put("version", "v1.0");
        response.put("security", "None");
        response.put("endpoints", "/api/v1.0/editors, /api/v1.0/topics, /api/v1.0/tags, /api/v1.0/messages");
        response.put("description", "Unprotected legacy API - use for backward compatibility");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/v2.0")
    public ResponseEntity<Map<String, String>> apiInfoV2() {
        Map<String, String> response = new HashMap<>();
        response.put("version", "v2.0");
        response.put("security", "JWT Token Required");
        response.put("registration", "POST /api/v2.0/editors");
        response.put("authentication", "POST /api/v2.0/login");
        response.put("publicEndpoints", "/api/v2.0/login, /api/v2.0/editors, /api/v2.0/test/**");
        response.put("protectedEndpoints", "All other /api/v2.0/** endpoints");
        response.put("description", "Protected modern API with role-based access control");
        response.put("roles", "ADMIN (full access), CUSTOMER (limited access)");
        response.put("testToken", "Use test users: admin/admin123, customer/customer123");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/security-info")
    public ResponseEntity<Map<String, Object>> securityInfo() {
        Map<String, Object> response = new HashMap<>();

        response.put("securityEnabled", true);
        response.put("authenticationMethods", new String[]{"JWT", "Basic Auth (temporary)"});
        response.put("jwtExpiration", "24 hours");
        response.put("passwordEncoding", "BCrypt");

        Map<String, String> testUsers = new HashMap<>();
        testUsers.put("admin", "admin/admin123 (ADMIN role)");
        testUsers.put("customer", "customer/customer123 (CUSTOMER role)");
        testUsers.put("user2", "user2/user2123 (CUSTOMER role)");
        response.put("testUsers", testUsers);

        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("publicV1", "/api/v1.0/** - No authentication");
        endpoints.put("publicV2", "/api/v2.0/login, /api/v2.0/editors, /api/v2.0/test/**");
        endpoints.put("protected", "/api/v2.0/** - JWT token required");
        response.put("endpoints", endpoints);

        return ResponseEntity.ok(response);
    }

}