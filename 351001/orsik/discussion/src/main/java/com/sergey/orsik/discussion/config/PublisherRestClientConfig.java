package com.sergey.orsik.discussion.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(PublisherProperties.class)
public class PublisherRestClientConfig {

    @Bean
    RestClient publisherRestClient(PublisherProperties properties) {
        String base = properties.baseUrl() != null ? properties.baseUrl().replaceAll("/$", "") : "http://localhost:24110";
        return RestClient.builder().baseUrl(base).build();
    }
}
