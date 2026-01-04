package com.blog.config;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class TestContainersInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final PostgreSQLContainer<?> postgresqlContainer;

    static {
        postgresqlContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15-alpine"))
                .withDatabaseName("blogdb_test")
                .withUsername("test")
                .withPassword("test")
                .withReuse(true);
        postgresqlContainer.start();
    }

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        TestPropertyValues.of(
                "spring.datasource.url=" + postgresqlContainer.getJdbcUrl(),
                "spring.datasource.username=" + postgresqlContainer.getUsername(),
                "spring.datasource.password=" + postgresqlContainer.getPassword(),
                "spring.datasource.driver-class-name=" + postgresqlContainer.getDriverClassName(),
                "spring.jpa.hibernate.ddl-auto=create-drop",
                "spring.liquibase.enabled=false"
        ).applyTo(context.getEnvironment());
    }

    public static PostgreSQLContainer<?> getPostgresqlContainer() {
        return postgresqlContainer;
    }
}