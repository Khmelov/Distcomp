package com.distcomp.config.web;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    @Qualifier("NoteServiceWebClient")
    public WebClient noteServiceWebClient() {
        return WebClient.builder()
                .baseUrl("")
                .build();
    }
}
