package com.lizaveta.notebook.security;

import com.lizaveta.notebook.model.UserRole;
import com.lizaveta.notebook.model.entity.Writer;
import com.lizaveta.notebook.repository.WriterRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final int AUTH_ERROR_CODE = 40101;
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final WriterRepository writerRepository;
    private final RestSecurityErrorWriter errorWriter;

    public JwtAuthenticationFilter(
            final JwtService jwtService,
            final WriterRepository writerRepository,
            final RestSecurityErrorWriter errorWriter) {
        this.jwtService = jwtService;
        this.writerRepository = writerRepository;
        this.errorWriter = errorWriter;
    }

    @Override
    protected void doFilterInternal(
            @NonNull final HttpServletRequest request,
            @NonNull final HttpServletResponse response,
            @NonNull final FilterChain filterChain) throws ServletException, IOException {
        if (!request.getRequestURI().startsWith("/api/v2.0")) {
            filterChain.doFilter(request, response);
            return;
        }
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = header.substring(BEARER_PREFIX.length()).trim();
        if (token.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            Claims claims = jwtService.parseValidClaimsOrThrow(token);
            String login = claims.getSubject();
            Writer writer = writerRepository.findByLogin(login)
                    .orElse(null);
            if (writer == null) {
                errorWriter.writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "User no longer exists", AUTH_ERROR_CODE);
                return;
            }
            UserRole userRole = writer.getRole();
            SecurityWriter principal = new SecurityWriter(
                    writer.getId(),
                    writer.getLogin(),
                    userRole);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    principal,
                    null,
                    principal.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            try {
                filterChain.doFilter(request, response);
            } finally {
                SecurityContextHolder.clearContext();
            }
        } catch (JwtException ex) {
            String message = jwtService.isExpiredException(ex) ? "Token has expired" : "Invalid or malformed token";
            errorWriter.writeError(response, HttpServletResponse.SC_UNAUTHORIZED, message, AUTH_ERROR_CODE);
        } catch (Exception ex) {
            errorWriter.writeError(
                    response, HttpServletResponse.SC_UNAUTHORIZED, "Token validation failed", AUTH_ERROR_CODE);
        }
    }
}
