package com.blog.config;

import com.blog.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String requestUri = request.getRequestURI();

        logger.debug("JwtAuthenticationFilter: URI={}, AuthHeader={}",
                requestUri, authHeader != null ? "present" : "absent");

        // Пропускаем сессионные endpoints
        if (requestUri.contains("/session-") || requestUri.equals("/api/v2.0/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Если есть JWT токен - обрабатываем его
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            final String jwt = authHeader.substring(7);

            try {
                final String userLogin = jwtService.extractUsername(jwt);
                logger.debug("Extracted username from JWT: {}", userLogin);

                if (userLogin != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(userLogin);

                    if (jwtService.validateToken(jwt, userDetails)) {
                        logger.debug("JWT token valid for user: {}", userLogin);

                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);

                        logger.debug("Authentication set from JWT for user: {}", userLogin);
                    } else {
                        logger.debug("JWT token validation failed for user: {}", userLogin);
                    }
                }
            } catch (Exception e) {
                logger.error("Error processing JWT token: {}", e.getMessage(), e);
            }
        }
        // Если нет JWT, но есть сессия - Spring Security сам установит аутентификацию
        else {
            logger.debug("No JWT token, relying on session authentication");
        }

        filterChain.doFilter(request, response);
    }
}