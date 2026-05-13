package by.bsuir.task361.discussion.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenService jwtTokenService;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;

    public JwtAuthenticationFilter(
            JwtTokenService jwtTokenService,
            JwtAuthenticationEntryPoint authenticationEntryPoint
    ) {
        this.jwtTokenService = jwtTokenService;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().startsWith("/api/v1.0/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || authorization.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }
        if (!authorization.startsWith("Bearer ")) {
            request.setAttribute("auth_error_message", "Invalid bearer token");
            authenticationEntryPoint.commence(
                    request,
                    response,
                    new InsufficientAuthenticationException("Invalid bearer token")
            );
            return;
        }
        String token = authorization.substring(7).trim();
        try {
            var claims = jwtTokenService.extractClaims(token);
            String login = claims.getSubject();
            String role = claims.get("role", String.class);
            if (login == null || login.isBlank() || role == null || role.isBlank()) {
                throw new BadCredentialsException("Invalid bearer token");
            }
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                var authentication = org.springframework.security.authentication.UsernamePasswordAuthenticationToken.authenticated(
                        login,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException exception) {
            request.setAttribute("auth_error_message", "Token expired");
            authenticationEntryPoint.commence(
                    request,
                    response,
                    new InsufficientAuthenticationException("Token expired", exception)
            );
        } catch (JwtException | IllegalArgumentException exception) {
            request.setAttribute("auth_error_message", "Invalid bearer token");
            authenticationEntryPoint.commence(
                    request,
                    response,
                    new InsufficientAuthenticationException("Invalid bearer token", exception)
            );
        } catch (AuthenticationException exception) {
            request.setAttribute("auth_error_message", "Invalid bearer token");
            authenticationEntryPoint.commence(request, response, exception);
        }
    }
}
