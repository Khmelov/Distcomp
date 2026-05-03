package by.liza.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${discussion.url}")
    private String discussionUrl;

    @Bean
    public WebClient discussionWebClient() {
        return WebClient.builder()
                .baseUrl(discussionUrl)
                .build();
    }
}