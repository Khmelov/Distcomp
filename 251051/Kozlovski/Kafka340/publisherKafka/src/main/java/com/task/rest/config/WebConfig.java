package com.task.rest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Настройка CORS для API publisher
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/v1.0/**")
                .allowedOriginPatterns("*")   // заменяет .allowedOrigins("*"), корректно работает с *
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }

    /**
     * WebClient для общения с модулем discussion.
     * discussion работает на порту 24130.
     */
    @Bean
    public WebClient discussionWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:24130/api/v1.0") // базовый URL для discussion
                .build();
    }
}