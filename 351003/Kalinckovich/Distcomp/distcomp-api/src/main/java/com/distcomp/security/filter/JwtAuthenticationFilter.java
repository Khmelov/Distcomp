package com.distcomp.security.filter;

import com.distcomp.utils.security.JwtUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    public static final String BEARER_PREFIX = "Bearer ";
    private final JwtUtil jwtUtil;

    @Override
    public @NonNull Mono<Void> filter(final @NonNull ServerWebExchange exchange,
                                      final @NonNull WebFilterChain chain) {
        final String authHeader = getAuthHeader(exchange);
        if (isBearerAuth(authHeader)) {
            final Mono<Void> usingJWT = authorizeUsingJWT(exchange, chain, authHeader);
            if (usingJWT != null) {
                return usingJWT;
            }
        }
        return chain.filter(exchange);
    }

    private Mono<Void> authorizeUsingJWT(final ServerWebExchange exchange, final WebFilterChain chain, final String authHeader) {
        Mono<Void> result = null;
        final String token = authHeader.substring(BEARER_PREFIX.length());
        if (jwtUtil.validateToken(token)) {
            result = fillContext(exchange, chain, token);
        }
        return result;
    }

    private Mono<Void> fillContext(final ServerWebExchange exchange, final WebFilterChain chain, final String token) {
        final String login = jwtUtil.extractLogin(token);
        final String role = jwtUtil.extractRole(token);
        final Authentication auth = new UsernamePasswordAuthenticationToken(
                login,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
        );
        final SecurityContext context = new SecurityContextImpl(auth);
        return chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));
    }

    private static boolean isBearerAuth(final String authHeader) {
        return authHeader != null && authHeader.startsWith(BEARER_PREFIX);
    }

    private static String getAuthHeader(final ServerWebExchange exchange) {
        return exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);
    }
}
