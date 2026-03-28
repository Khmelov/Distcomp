package com.distcomp.client

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@Configuration
class RestClientConfig {
    @Bean
    fun noticeRestClient(): RestClient =
        RestClient.create("http://localhost:24130")
}