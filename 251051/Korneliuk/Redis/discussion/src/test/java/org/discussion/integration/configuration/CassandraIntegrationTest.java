package org.discussion.integration.configuration;

import com.datastax.oss.driver.api.core.CqlSession;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
public abstract class   CassandraIntegrationTest {

    @Container
    static CassandraContainer<?> cassandra = new CassandraContainer<>("cassandra:4.1")
            .withEnv("HEAP_NEWSIZE", "128M")
            .withEnv("MAX_HEAP_SIZE", "1024M")
            .waitingFor(Wait.forLogMessage(".*Starting listening for CQL clients.*", 1));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.cassandra.contact-points", cassandra::getHost);
        registry.add("spring.cassandra.port", () -> cassandra.getMappedPort(9042));
        registry.add("spring.cassandra.local-datacenter", () -> "datacenter1");
        registry.add("spring.cassandra.keyspace-name", () -> "distcomp");

        // Liquibase настройки
        registry.add("cassandra.liquibase.url", () ->
                "jdbc:cassandra://" + cassandra.getHost() + ":" + cassandra.getMappedPort(9042) +
                        "/system?localdatacenter=datacenter1");
        registry.add("cassandra.liquibase.change-log", () ->
                "classpath:db/changelog/changelog-master-cassandra.xml");
    }

    @BeforeAll
    static void initCassandraSchema() {
        try (CqlSession session = CqlSession.builder()
                .addContactPoint(new InetSocketAddress(
                        cassandra.getHost(),
                        cassandra.getMappedPort(9042)))
                .withLocalDatacenter("datacenter1")
                .build()) {

            session.execute("CREATE KEYSPACE IF NOT EXISTS distcomp " +
                    "WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}");
        }

        // Запустить Liquibase
        String liquibaseUrl = "jdbc:cassandra://" +
                cassandra.getHost() + ":" + cassandra.getMappedPort(9042) +
                "/distcomp?compliancemode=Liquibase&localdatacenter=datacenter1";

        try (Connection conn = DriverManager.getConnection(liquibaseUrl, "cassandra", "cassandra")) {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(conn));

            Liquibase liquibase = new Liquibase(
                    "db/changelog/changelog-master-cassandra.xml",
                    new ClassLoaderResourceAccessor(),
                    database
            );

            System.out.println("Cassandra mapped port: " + cassandra.getMappedPort(9042));
            System.out.println("Cassandra host: " + cassandra.getHost());

            liquibase.update(new Contexts(), new LabelExpression(), false);
        } catch (Exception e) {
            throw new RuntimeException("Liquibase migration failed", e);
        }
    }
}