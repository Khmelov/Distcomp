package com.apigateway.security;

import org.reactivestreams.Publisher;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

@Component
public class V2ErrorResponseWebFilter implements WebFilter, Ordered {

    private final ObjectMapper objectMapper;

    public V2ErrorResponseWebFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().pathWithinApplication().value();
        if (!path.startsWith("/api/v2.0/")) {
            return chain.filter(exchange);
        }

        ServerHttpResponse originalResponse = exchange.getResponse();
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (!shouldNormalize(getDelegate())) {
                    return super.writeWith(body);
                }

                return DataBufferUtils.join(Flux.from(body))
                        .flatMap(dataBuffer -> {
                            byte[] originalBytes = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(originalBytes);
                            DataBufferUtils.release(dataBuffer);

                            byte[] responseBytes = normalizeErrorBody(originalBytes, getDelegate().getStatusCode());
                            getDelegate().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                            getDelegate().getHeaders().setContentLength(responseBytes.length);
                            return super.writeWith(Mono.just(getDelegate().bufferFactory().wrap(responseBytes)));
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

    private boolean shouldNormalize(ServerHttpResponse response) {
        HttpStatusCode status = response.getStatusCode();
        return status != null && status.isError();
    }

    private byte[] normalizeErrorBody(byte[] body, HttpStatusCode status) {
        try {
            String rawBody = new String(body, StandardCharsets.UTF_8);
            if (rawBody.contains("\"errorMessage\"") && rawBody.contains("\"errorCode\"")) {
                return body;
            }

            String message = extractMessage(rawBody);
            String code = status.value() + "01";
            return objectMapper.writeValueAsBytes(new ErrorResponse(message, code));
        } catch (Exception e) {
            return ("{\"errorMessage\":\"Request failed\",\"errorCode\":\"" + status.value() + "01\"}")
                    .getBytes(StandardCharsets.UTF_8);
        }
    }

    private String extractMessage(String rawBody) {
        try {
            JsonNode json = objectMapper.readTree(rawBody);
            if (!json.path("error").isMissingNode()) {
                return json.path("error").asText();
            }
            if (!json.path("message").isMissingNode()) {
                return json.path("message").asText();
            }
        } catch (Exception ignored) {
            // Fall back to raw body below.
        }
        return rawBody == null || rawBody.isBlank() ? "Request failed" : rawBody;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 10;
    }
}
