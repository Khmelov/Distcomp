package by.bsuir.task330.publisher.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class PublisherClientConfig {

    @Bean
    public RestClient discussionRestClient(@Value("${discussion.base-url}") String discussionBaseUrl) {
        return RestClient.builder()
                .baseUrl(discussionBaseUrl)
                .build();
    }
}
