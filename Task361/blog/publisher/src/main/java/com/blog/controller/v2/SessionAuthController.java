package com.blog.controller.v2;

import com.blog.config.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v2.0/session")
public class SessionAuthController {

    private static final Logger logger = LoggerFactory.getLogger(SessionAuthController.class);

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> sessionLogin(HttpServletRequest request) {
        // Этот endpoint будет обрабатываться Spring Security formLogin
        // Просто возвращаем информацию о сессии
        HttpSession session = request.getSession(false);

        Map<String, Object> response = new HashMap<>();
        if (session != null) {
            response.put("status", "authenticated");
            response.put("sessionId", session.getId());
            response.put("username", SecurityUtils.getCurrentUserLogin());
            response.put("userId", SecurityUtils.getCurrentUserId());
            return ResponseEntity.ok(response);
        }

        response.put("status", "not_authenticated");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> response = new HashMap<>();
        response.put("hasSession", session != null);
        response.put("sessionId", session != null ? session.getId() : null);
        response.put("isAuthenticated", SecurityUtils.isAuthenticated());
        response.put("username", SecurityUtils.getCurrentUserLogin());
        response.put("userId", SecurityUtils.getCurrentUserId());
        response.put("authentication", auth != null ? auth.getName() : null);
        response.put("authorities", auth != null ? auth.getAuthorities() : null);

        logger.debug("Session check: {}", response);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> sessionLogout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        SecurityContextHolder.clearContext();

        Map<String, String> response = new HashMap<>();
        response.put("status", "logged_out");
        return ResponseEntity.ok(response);
    }
}