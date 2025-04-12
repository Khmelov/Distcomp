package com.example.rest.webClient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class DiscussionMessageClient {
    @Bean
    public WebClient messageDiscussionWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:24130/api/v1.0/messages")
                .build();
    }
}
