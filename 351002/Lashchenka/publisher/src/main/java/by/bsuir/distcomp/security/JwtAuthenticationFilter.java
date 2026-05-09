package by.bsuir.distcomp.security;

import by.bsuir.distcomp.model.EditorRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (!request.getRequestURI().startsWith("/api/v2.0/")) {
            filterChain.doFilter(request, response);
            return;
        }
        if ("POST".equalsIgnoreCase(request.getMethod())
                && ("/api/v2.0/login".equals(request.getRequestURI())
                    || "/api/v2.0/editors".equals(request.getRequestURI()))) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = header.substring(7);
        try {
            Claims claims = jwtService.parseAndValidate(token);
            String login = claims.getSubject();
            String roleStr = claims.get("role", String.class);
            Number eid = (Number) claims.get("eid");
            if (login == null || roleStr == null || eid == null) {
                throw new JwtException("Invalid claims");
            }
            EditorRole role = EditorRole.valueOf(roleStr);
            EditorAuthPrincipal principal = new EditorAuthPrincipal(eid.longValue(), login, role, "");
            var auth = new UsernamePasswordAuthenticationToken(
                    principal, null, principal.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (JwtException | IllegalArgumentException ignored) {
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
    }
}
