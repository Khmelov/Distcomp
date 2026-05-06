package com.sergey.orsik.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(DiscussionProperties.class)
public class DiscussionRestClientConfig {

    @Bean
    RestClient discussionRestClient(DiscussionProperties properties) {
        String base = properties.baseUrl() != null ? properties.baseUrl().replaceAll("/$", "") : "http://localhost:24130";
        return RestClient.builder().baseUrl(base).build();
    }
}
