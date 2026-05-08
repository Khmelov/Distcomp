package com.apigateway.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class WriterIdentityClient {

    private final WebClient webClient;

    public WriterIdentityClient(
            WebClient.Builder webClientBuilder,
            @Value("${app.security.services.writer-url}") String writerServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(writerServiceUrl).build();
    }

    public Mono<WriterIdentityResponse> findByLogin(String login) {
        return webClient.get()
                .uri("/api/v1.0/internal/writers/by-login/{login}", login)
                .retrieve()
                .bodyToMono(WriterIdentityResponse.class);
    }
}
