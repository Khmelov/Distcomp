package com.github.Lexya06.startrestapp.publisher.impl.integration.discussion.config;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class DiscussionWebClientConfig {

    @Getter
    @Value("${services.discussion.url}")
    private String discussionBaseUrl;

    @Bean(name = "discussionWebClient")
    public WebClient discussionWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(discussionBaseUrl)
                .build();
    }
}