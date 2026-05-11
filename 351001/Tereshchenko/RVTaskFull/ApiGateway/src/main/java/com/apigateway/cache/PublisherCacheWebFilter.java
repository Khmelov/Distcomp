package com.apigateway.cache;

import org.reactivestreams.Publisher;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class PublisherCacheWebFilter implements WebFilter, Ordered {

    private final PublisherCacheService cacheService;

    public PublisherCacheWebFilter(PublisherCacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (!cacheService.isEnabled() || !isCacheableApiPath(exchange)) {
            return chain.filter(exchange);
        }

        if (exchange.getRequest().getMethod() == HttpMethod.GET) {
            return handleGet(exchange, chain);
        }

        return chain.filter(exchange)
                .then(Mono.defer(() -> isSuccessfulWrite(exchange)
                        ? cacheService.evictAll().onErrorResume(error -> Mono.just(0L)).then()
                        : Mono.empty()));
    }

    private Mono<Void> handleGet(ServerWebExchange exchange, WebFilterChain chain) {
        String key = cacheService.buildKey(
                exchange.getRequest().getMethod().name(),
                exchange.getRequest().getPath().pathWithinApplication().value(),
                exchange.getRequest().getURI().getRawQuery()
        );

        return cacheService.get(key)
                .flatMap(cachedBody -> writeCachedResponse(exchange, cachedBody).thenReturn(true))
                .switchIfEmpty(Mono.defer(() -> writeAndCacheResponse(exchange, chain, key).thenReturn(true)))
                .then();
    }

    private Mono<Void> writeCachedResponse(ServerWebExchange exchange, String cachedBody) {
        byte[] bytes = cachedBody.getBytes(StandardCharsets.UTF_8);
        ServerHttpResponse response = exchange.getResponse();

        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        response.getHeaders().setContentLength(bytes.length);
        response.getHeaders().set("X-Cache", "HIT");

        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }

    private Mono<Void> writeAndCacheResponse(ServerWebExchange exchange, WebFilterChain chain, String key) {
        ServerHttpResponse originalResponse = exchange.getResponse();
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (!isSuccessfulJsonResponse(getDelegate())) {
                    return super.writeWith(body);
                }

                return DataBufferUtils.join(Flux.from(body))
                        .flatMap(dataBuffer -> {
                            byte[] bytes = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(bytes);
                            DataBufferUtils.release(dataBuffer);

                            String responseBody = new String(bytes, StandardCharsets.UTF_8);
                            DataBuffer responseBuffer = getDelegate().bufferFactory().wrap(bytes);
                            getDelegate().getHeaders().set("X-Cache", "MISS");

                            return cacheService.put(key, responseBody)
                                    .onErrorResume(error -> Mono.just(false))
                                    .then(super.writeWith(Mono.just(responseBuffer)));
                        })
                        .switchIfEmpty(super.writeWith(Mono.empty()));
            }

            @Override
            public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
                return writeWith(Flux.from(body).flatMapSequential(dataBuffers -> dataBuffers));
            }
        };

        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    private boolean isCacheableApiPath(ServerWebExchange exchange) {
        String path = exchange.getRequest().getPath().pathWithinApplication().value();
        return path.startsWith("/api/v1.0/writers")
                || path.startsWith("/api/v1.0/tweets")
                || path.startsWith("/api/v1.0/markers")
                || path.startsWith("/api/v1.0/messages")
                || path.startsWith("/api/v1.0/tweet-markers");
    }

    private boolean isSuccessfulWrite(ServerWebExchange exchange) {
        HttpMethod method = exchange.getRequest().getMethod();
        return method != HttpMethod.GET
                && exchange.getResponse().getStatusCode() != null
                && exchange.getResponse().getStatusCode().is2xxSuccessful();
    }

    private boolean isSuccessfulJsonResponse(ServerHttpResponse response) {
        MediaType contentType = response.getHeaders().getContentType();
        return response.getStatusCode() != null
                && response.getStatusCode().value() == HttpStatus.OK.value()
                && (contentType == null || MediaType.APPLICATION_JSON.isCompatibleWith(contentType));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
