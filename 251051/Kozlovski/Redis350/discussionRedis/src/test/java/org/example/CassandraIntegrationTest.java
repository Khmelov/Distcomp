package org.example;

import com.datastax.oss.driver.api.core.CqlSession;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.net.InetSocketAddress;
import java.time.Duration;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class CassandraIntegrationTest {

    @Container
    protected static final CassandraContainer<?> cassandra =
            new CassandraContainer<>("cassandra:4.1.1")
                    .withStartupTimeout(Duration.ofSeconds(120));

    @DynamicPropertySource
    static void registerCassandraProperties(DynamicPropertyRegistry registry) {

        cassandra.start();

        String host = cassandra.getHost();
        int port = cassandra.getFirstMappedPort();

        registry.add("spring.cassandra.contact-points", () -> host);
        registry.add("spring.cassandra.port", () -> port);
        registry.add("spring.cassandra.local-datacenter", () -> "datacenter1");
        registry.add("spring.cassandra.keyspace-name", () -> "distcomp");
        registry.add("spring.cassandra.schema-action", () -> "NONE");

        try (CqlSession session = CqlSession.builder()
                .addContactPoint(new InetSocketAddress(host, port))
                .withLocalDatacenter("datacenter1")
                .build()) {

            session.execute("""
                    CREATE KEYSPACE IF NOT EXISTS distcomp
                    WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}
                    AND durable_writes = true;
                    """);

            session.execute("""
                    CREATE TABLE IF NOT EXISTS distcomp.tbl_comment (
                        country text,
                        tweet_id bigint,
                        id bigint,
                        content text,
                        PRIMARY KEY ((country), tweet_id, id)
                    );
                    """);

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Cassandra schema", e);
        }
    }

    @BeforeAll
    void waitContainer() {
        assert cassandra.isRunning();
    }
}