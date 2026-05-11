package org.example.newsapi;

import org.junit.jupiter.api.Disabled;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Disabled
public abstract class AbstractIntegrationTest {

    // Определяем контейнер PostgreSQL (версия 15-alpine)
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("distcomp")
            .withUsername("postgres")
            .withPassword("postgres");

    // Динамически подменяем настройки application.properties на настройки контейнера
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> postgres.getJdbcUrl() + "?currentSchema=distcomp");
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        // Указываем Hibernate и Liquibase использовать схему по умолчанию public (так проще в тестах)
        // или ту, что мы создали. Если скрипт Liquibase требует схему distcomp,
        // нам нужно убедиться, что она создана.
        // Но TestContainers создает пустую БД.
        // Hibernate валидирует схему.
        // Проще всего переопределить схему на public для тестов,
        // либо добавить инициализирующий скрипт для создания схемы.

        // В данном случае мы используем URL с параметром currentSchema=distcomp.
        // Postgres в тестконтейнере может не иметь этой схемы.
        // Поэтому добавим команду на создание схемы при старте контейнера:
        postgres.withInitScript("db/test-init-schema.sql");
    }
}