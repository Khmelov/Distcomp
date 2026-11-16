package by.bsuir.rest.support;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class PostgresTestSupport {

    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "postgres";
    private static PostgreSQLContainer<?> container;
    private static EmbeddedPostgres embedded;
    private static final String JDBC_URL = startDatabase();

    @DynamicPropertySource
    static void registerPostgres(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> JDBC_URL);
        registry.add("spring.datasource.username", () -> USERNAME);
        registry.add("spring.datasource.password", () -> PASSWORD);
    }

    private static String startDatabase() {
        if (dockerAvailable()) {
            container = new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("distcomp")
                    .withUsername(USERNAME)
                    .withPassword(PASSWORD);
            container.start();
            return container.getJdbcUrl();
        }
        try {
            embedded = EmbeddedPostgres.builder().start();
            return embedded.getJdbcUrl(USERNAME, "postgres");
        } catch (Exception exception) {
            throw new IllegalStateException("PostgreSQL for tests is unavailable", exception);
        }
    }

    private static boolean dockerAvailable() {
        try {
            return DockerClientFactory.instance().isDockerAvailable();
        } catch (Throwable exception) {
            return false;
        }
    }
}
