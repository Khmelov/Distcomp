package com.apigateway.security;

import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.function.BiFunction;

@Component
public class V2SecurityWebFilter implements WebFilter, Ordered {

    private static final String V2_PREFIX = "/api/v2.0";

    private final JwtService jwtService;
    private final WriterIdentityClient writerIdentityClient;
    private final ResourceOwnershipClient ownershipClient;
    private final ObjectMapper objectMapper;

    public V2SecurityWebFilter(
            JwtService jwtService,
            WriterIdentityClient writerIdentityClient,
            ResourceOwnershipClient ownershipClient,
            ObjectMapper objectMapper) {
        this.jwtService = jwtService;
        this.writerIdentityClient = writerIdentityClient;
        this.ownershipClient = ownershipClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = path(exchange);
        if (!path.startsWith(V2_PREFIX) || isPublicEndpoint(exchange)) {
            return chain.filter(exchange);
        }

        String authorization = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return writeError(exchange, HttpStatus.UNAUTHORIZED, "Bearer token is required", "40101");
        }

        JwtClaims claims;
        try {
            claims = jwtService.validate(authorization.substring("Bearer ".length()));
        } catch (IllegalArgumentException e) {
            return writeError(exchange, HttpStatus.UNAUTHORIZED, "Invalid or expired token", "40102");
        }

        return writerIdentityClient.findByLogin(claims.login())
                .map(identity -> identity.toAuthUser(claims.role()))
                .onErrorResume(error -> Mono.empty())
                .flatMap(user -> {
                    exchange.getAttributes().put(AuthController.AUTH_USER_ATTRIBUTE, user);
                    return authorize(exchange, chain, user);
                })
                .switchIfEmpty(Mono.defer(() -> writeError(exchange, HttpStatus.UNAUTHORIZED, "Authenticated writer was not found", "40103")));
    }

    private Mono<Void> authorize(ServerWebExchange exchange, WebFilterChain chain, AuthUser user) {
        if (user.isAdmin() || exchange.getRequest().getMethod() == HttpMethod.GET) {
            return chain.filter(exchange);
        }

        String path = path(exchange);
        HttpMethod method = exchange.getRequest().getMethod();

        if (path.startsWith(V2_PREFIX + "/writers/")) {
            Long writerId = firstPathId(path, V2_PREFIX + "/writers/");
            return writerId != null && writerId.equals(user.id())
                    ? chain.filter(exchange)
                    : writeForbidden(exchange);
        }

        if (path.equals(V2_PREFIX + "/tweets") && method == HttpMethod.POST) {
            return authorizeBodyWriterId(exchange, chain, user);
        }

        if (path.startsWith(V2_PREFIX + "/tweets/by-writer/") && method == HttpMethod.DELETE) {
            Long writerId = firstPathId(path, V2_PREFIX + "/tweets/by-writer/");
            return writerId != null && writerId.equals(user.id())
                    ? chain.filter(exchange)
                    : writeForbidden(exchange);
        }

        if (path.startsWith(V2_PREFIX + "/tweets/") && method == HttpMethod.PUT) {
            Long tweetId = firstPathId(path, V2_PREFIX + "/tweets/");
            return authorizeTweetOwner(tweetId, user)
                    .flatMap(allowed -> allowed
                            ? authorizeBodyWriterId(exchange, chain, user)
                            : writeForbidden(exchange));
        }

        if (path.startsWith(V2_PREFIX + "/tweets/") && method == HttpMethod.DELETE) {
            Long tweetId = firstPathId(path, V2_PREFIX + "/tweets/");
            return authorizeTweetOwner(tweetId, user)
                    .flatMap(allowed -> allowed ? chain.filter(exchange) : writeForbidden(exchange));
        }

        if (path.equals(V2_PREFIX + "/messages") && method == HttpMethod.POST) {
            return authorizeBodyTweetOwner(exchange, chain, user);
        }

        if (path.startsWith(V2_PREFIX + "/messages/") && method == HttpMethod.PUT) {
            return authorizeBodyTweetOwner(exchange, chain, user);
        }

        if (path.startsWith(V2_PREFIX + "/messages/tweets/") && method == HttpMethod.DELETE) {
            Long tweetId = firstPathId(path, V2_PREFIX + "/messages/tweets/");
            return authorizeTweetOwner(tweetId, user)
                    .flatMap(allowed -> allowed ? chain.filter(exchange) : writeForbidden(exchange));
        }

        if (path.startsWith(V2_PREFIX + "/messages/") && method == HttpMethod.DELETE) {
            Long messageId = firstPathId(path, V2_PREFIX + "/messages/");
            return authorizeMessageOwner(messageId, user)
                    .flatMap(allowed -> allowed ? chain.filter(exchange) : writeForbidden(exchange));
        }

        return writeForbidden(exchange);
    }

    private Mono<Void> authorizeBodyWriterId(ServerWebExchange exchange, WebFilterChain chain, AuthUser user) {
        return withCachedBody(exchange, (body, cachedExchange) -> {
            Long writerId = readLong(body, "writerId");
            return writerId != null && writerId.equals(user.id())
                    ? chain.filter(cachedExchange)
                    : writeForbidden(exchange);
        });
    }

    private Mono<Void> authorizeBodyTweetOwner(ServerWebExchange exchange, WebFilterChain chain, AuthUser user) {
        return withCachedBody(exchange, (body, cachedExchange) -> {
            Long tweetId = readLong(body, "tweetId");
            if (tweetId == null) {
                return writeForbidden(exchange);
            }
            return authorizeTweetOwner(tweetId, user)
                    .flatMap(allowed -> allowed ? chain.filter(cachedExchange) : writeForbidden(exchange));
        });
    }

    private Mono<Boolean> authorizeTweetOwner(Long tweetId, AuthUser user) {
        if (tweetId == null) {
            return Mono.just(false);
        }
        return ownershipClient.findTweetWriterId(tweetId)
                .map(user.id()::equals)
                .onErrorReturn(false);
    }

    private Mono<Boolean> authorizeMessageOwner(Long messageId, AuthUser user) {
        if (messageId == null) {
            return Mono.just(false);
        }
        return ownershipClient.findMessageTweetId(messageId)
                .flatMap(tweetId -> authorizeTweetOwner(tweetId, user))
                .onErrorReturn(false);
    }

    private Mono<Void> withCachedBody(
            ServerWebExchange exchange,
            BiFunction<String, ServerWebExchange, Mono<Void>> handler) {
        return DataBufferUtils.join(exchange.getRequest().getBody())
                .defaultIfEmpty(exchange.getResponse().bufferFactory().wrap(new byte[0]))
                .flatMap(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);

                    String body = new String(bytes, StandardCharsets.UTF_8);
                    ServerHttpRequestDecorator decoratedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
                        @Override
                        public Flux<DataBuffer> getBody() {
                            return Flux.defer(() -> Flux.just(exchange.getResponse().bufferFactory().wrap(bytes)));
                        }
                    };

                    return handler.apply(body, exchange.mutate().request(decoratedRequest).build());
                });
    }

    private Long readLong(String body, String fieldName) {
        try {
            JsonNode json = objectMapper.readTree(body);
            JsonNode value = json.path(fieldName);
            if (value.isMissingNode() || value.isNull()) {
                return null;
            }
            return value.asLong();
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isPublicEndpoint(ServerWebExchange exchange) {
        String path = path(exchange);
        HttpMethod method = exchange.getRequest().getMethod();
        return (path.equals(V2_PREFIX + "/login") && method == HttpMethod.POST)
                || (path.equals(V2_PREFIX + "/writers") && method == HttpMethod.POST);
    }

    private String path(ServerWebExchange exchange) {
        return exchange.getRequest().getPath().pathWithinApplication().value();
    }

    private Long firstPathId(String path, String prefix) {
        try {
            String value = path.substring(prefix.length()).split("/")[0];
            return Long.parseLong(value);
        } catch (Exception e) {
            return null;
        }
    }

    private Mono<Void> writeForbidden(ServerWebExchange exchange) {
        return writeError(exchange, HttpStatus.FORBIDDEN, "Access denied", "40301");
    }

    private Mono<Void> writeError(ServerWebExchange exchange, HttpStatus status, String message, String code) {
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(new ErrorResponse(message, code));
            exchange.getResponse().setStatusCode(status);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            exchange.getResponse().getHeaders().setContentLength(bytes.length);
            return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
        } catch (Exception e) {
            exchange.getResponse().setStatusCode(status);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
