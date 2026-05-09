package by.liza.discussion.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${publisher.url}")
    private String publisherUrl;

    @Bean
    public WebClient publisherWebClient() {
        return WebClient.builder()
                .baseUrl(publisherUrl)
                .build();
    }
}