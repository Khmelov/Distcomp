package com.blog.controller.v2;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v2.0/test")
public class TestControllerV2 {

    @GetMapping("/public")
    public ResponseEntity<Map<String, String>> publicEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "This is a public endpoint in v2.0");
        response.put("security", "No authentication required");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/protected")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> protectedEndpoint() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "This is a protected endpoint in v2.0");
        response.put("security", "JWT authentication required");
        response.put("username", auth.getName());
        response.put("authorities", auth.getAuthorities());
        response.put("authenticated", auth.isAuthenticated());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> adminEndpoint() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "This is an ADMIN-only endpoint in v2.0");
        response.put("security", "ADMIN role required");
        response.put("username", auth.getName());
        response.put("role", "ADMIN");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Map<String, Object>> customerEndpoint() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "This is a CUSTOMER-only endpoint in v2.0");
        response.put("security", "CUSTOMER role required");
        response.put("username", auth.getName());
        response.put("role", "CUSTOMER");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user-info")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getUserInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> response = new HashMap<>();
        response.put("username", auth.getName());
        response.put("authorities", auth.getAuthorities());
        response.put("principalClass", auth.getPrincipal().getClass().getSimpleName());
        response.put("authenticated", auth.isAuthenticated());

        return ResponseEntity.ok(response);
    }
}