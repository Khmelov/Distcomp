package com.example.publisher.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AppConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        // Регистрируем модуль для работы с Java Time API
        objectMapper.registerModule(new JavaTimeModule());

        // Отключаем запись дат как timestamp (чтобы были строки)
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Для отладки можно включить
        // objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        return objectMapper;
    }
}