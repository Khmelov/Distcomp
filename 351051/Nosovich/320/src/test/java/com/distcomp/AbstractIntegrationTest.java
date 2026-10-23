package com.distcomp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Base class of the controller integration tests.
 *
 * <p>The tests talk to a real PostgreSQL server, so the Liquibase changelog and
 * the JPQL/Criteria queries of the application are exercised exactly as in
 * production. A dedicated database {@code distcomp_test} is used and created on
 * demand, therefore the tests never touch the data of the running application.</p>
 *
 * <p>Prerequisite: the PostgreSQL server described in the README must be
 * listening on {@code localhost:5432} (user {@code postgres}, password
 * {@code postgres}).</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
public abstract class AbstractIntegrationTest {

    private static final String TEST_DATABASE = "distcomp_test";

    private static final String SERVER_URL = "jdbc:postgresql://localhost:5432/postgres";

    private static final String TEST_URL = "jdbc:postgresql://localhost:5432/" + TEST_DATABASE;

    static {
        ensureTestDatabaseExists();
    }

    @Autowired
    protected MockMvc mockMvc;

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> TEST_URL);
    }

    /** Creates the dedicated test database when it is missing. */
    private static void ensureTestDatabaseExists() {
        try (Connection connection = DriverManager.getConnection(SERVER_URL, "postgres", "postgres");
             Statement statement = connection.createStatement()) {

            try (ResultSet existing = statement.executeQuery(
                    "SELECT 1 FROM pg_database WHERE datname = '" + TEST_DATABASE + "'")) {
                if (!existing.next()) {
                    statement.execute("CREATE DATABASE " + TEST_DATABASE);
                }
            }
        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "PostgreSQL on localhost:5432 is required to run the integration tests", exception);
        }
    }

    /** @return a suffix that is unique inside the shared test database */
    protected String uniqueSuffix() {
        return Long.toString(System.nanoTime());
    }
}
