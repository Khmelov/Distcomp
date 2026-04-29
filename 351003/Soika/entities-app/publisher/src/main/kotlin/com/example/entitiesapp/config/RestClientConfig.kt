package com.example.entitiesapp.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@Configuration
class RestClientConfig {
    @Bean
    fun discussionClient(): RestClient {
        return RestClient.builder()
            .baseUrl("http://localhost:24130${ApiConfig.API_V1}")
            .build()
    }
}