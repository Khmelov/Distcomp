package by.bsuir.task361.publisher.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenService jwtTokenService;
    private final PublisherUserDetailsService userDetailsService;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;

    public JwtAuthenticationFilter(
            JwtTokenService jwtTokenService,
            PublisherUserDetailsService userDetailsService,
            JwtAuthenticationEntryPoint authenticationEntryPoint
    ) {
        this.jwtTokenService = jwtTokenService;
        this.userDetailsService = userDetailsService;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/api/v1.0/")
                || (path.equals("/api/v2.0/login") && HttpMethod.POST.matches(request.getMethod()))
                || (path.equals("/api/v2.0/users") && HttpMethod.POST.matches(request.getMethod()));
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
            String login = jwtTokenService.extractLogin(token);
            if (login == null || login.isBlank()) {
                throw new BadCredentialsException("Invalid bearer token");
            }
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                filterChain.doFilter(request, response);
                return;
            }
            UserDetails userDetails = userDetailsService.loadUserByUsername(login);
            var authentication = org.springframework.security.authentication.UsernamePasswordAuthenticationToken.authenticated(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
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
