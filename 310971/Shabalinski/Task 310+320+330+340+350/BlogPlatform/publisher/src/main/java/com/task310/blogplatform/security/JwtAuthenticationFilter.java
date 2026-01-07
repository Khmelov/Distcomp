package com.task310.blogplatform.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            
            // Skip if token is empty or too short to be valid
            if (jwt == null || jwt.trim().isEmpty() || jwt.length() < 10) {
                filterChain.doFilter(request, response);
                return;
            }
            
            final String login = jwtUtil.getLoginFromToken(jwt);
            final com.task310.blogplatform.model.Role role = jwtUtil.getRoleFromToken(jwt);

            if (login != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (jwtUtil.validateToken(jwt, login)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            login,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()))
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Check if it's a JWT-related exception or IllegalArgumentException
            String exceptionName = e.getClass().getName();
            if (exceptionName.contains("jsonwebtoken") || e instanceof IllegalArgumentException) {
                // Invalid token - silently continue without authentication
                // This is expected for unauthenticated requests
                // No logging needed for invalid tokens
            } else {
                // Other unexpected errors - log but don't fail the request
                logger.warn("Unexpected error processing JWT token", e);
            }
        }

        filterChain.doFilter(request, response);
    }
}

