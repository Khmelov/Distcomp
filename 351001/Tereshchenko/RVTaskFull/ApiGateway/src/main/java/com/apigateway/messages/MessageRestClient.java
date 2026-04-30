package com.apigateway.messages;

import com.apigateway.messages.dto.MessageRequestTo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class MessageRestClient {

    private final WebClient webClient;

    public MessageRestClient(
            WebClient.Builder webClientBuilder,
            @Value("${app.security.services.message-url}") String messageServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(messageServiceUrl).build();
    }

    public Mono<ResponseEntity<String>> proxyGet(ServerWebExchange exchange) {
        String path = toV1MessagePath(exchange);
        String query = exchange.getRequest().getURI().getRawQuery();

        return exchange(HttpMethod.GET, query == null ? path : path + "?" + query, null);
    }

    public Mono<ResponseEntity<String>> create(MessageRequestTo request) {
        return exchange(HttpMethod.POST, "/api/v1.0/messages", request);
    }

    public Mono<ResponseEntity<String>> update(Long id, MessageRequestTo request) {
        return exchange(HttpMethod.PUT, "/api/v1.0/messages/" + id, request);
    }

    public Mono<ResponseEntity<String>> deleteById(Long id) {
        return exchange(HttpMethod.DELETE, "/api/v1.0/messages/" + id, null);
    }

    public Mono<ResponseEntity<String>> deleteByTweetId(Long tweetId) {
        return exchange(HttpMethod.DELETE, "/api/v1.0/messages/tweets/" + tweetId, null);
    }

    private Mono<ResponseEntity<String>> exchange(HttpMethod method, String uri, Object body) {
        WebClient.RequestBodySpec request = webClient.method(method).uri(uri);
        WebClient.RequestHeadersSpec<?> requestSpec = body == null ? request : request.bodyValue(body);

        return requestSpec.exchangeToMono(response -> response.bodyToMono(String.class)
                        .defaultIfEmpty("")
                        .map(responseBody -> toResponse(response.statusCode(), responseBody)))
                .onErrorResume(error -> Mono.just(ResponseEntity.status(503).body(error.getMessage())));
    }

    private ResponseEntity<String> toResponse(HttpStatusCode statusCode, String body) {
        if (body == null || body.isBlank()) {
            return ResponseEntity.status(statusCode).build();
        }
        return ResponseEntity.status(statusCode)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    private String toV1MessagePath(ServerWebExchange exchange) {
        String path = exchange.getRequest().getPath().pathWithinApplication().value();
        if (path.startsWith("/api/v2.0/")) {
            return path.replaceFirst("/api/v2\\.0", "/api/v1.0");
        }
        return path;
    }
}
