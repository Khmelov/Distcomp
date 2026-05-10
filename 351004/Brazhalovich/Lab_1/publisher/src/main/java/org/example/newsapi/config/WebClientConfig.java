package org.example.newsapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient discussionWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:24130")   // только хост+порт
                .build();
    }
}