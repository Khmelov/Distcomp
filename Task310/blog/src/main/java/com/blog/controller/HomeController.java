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
        response.put("version", "v1.0");
        response.put("status", "OK");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/v1.0")
    public ResponseEntity<Map<String, String>> apiInfo() {
        Map<String, String> response = new HashMap<>();
        response.put("endpoints", "/api/v1.0/editors, /api/v1.0/topics, /api/v1.0/tags, /api/v1.0/messages");
        response.put("port", "24110");
        return ResponseEntity.ok(response);
    }
}