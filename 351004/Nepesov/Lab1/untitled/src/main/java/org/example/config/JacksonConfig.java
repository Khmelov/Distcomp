package org.example.config;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> {
            builder.featuresToDisable(com.fasterxml.jackson.databind.SerializationFeature.WRAP_ROOT_VALUE);
            builder.featuresToDisable(com.fasterxml.jackson.databind.DeserializationFeature.UNWRAP_ROOT_VALUE);
        };
    }
}