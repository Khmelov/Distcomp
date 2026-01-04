package com.example.storyapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Запускается на порту 24110.
 * Использует Liquibase для инициализации схемы в PostgreSQL.
 */
@SpringBootApplication
public class StoryApplication {
    public static void main(String[] args) {
        SpringApplication.run(StoryApplication.class, args);
    }
}