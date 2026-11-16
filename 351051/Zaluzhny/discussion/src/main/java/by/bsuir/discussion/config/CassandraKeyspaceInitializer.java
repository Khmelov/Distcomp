package by.bsuir.discussion.config;

import com.datastax.oss.driver.api.core.CqlSession;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetSocketAddress;

public class CassandraKeyspaceInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        Environment environment = context.getEnvironment();
        ensureKeyspace(
                environment.getProperty("spring.cassandra.contact-points", "localhost"),
                environment.getProperty("spring.cassandra.port", Integer.class, 9042),
                environment.getProperty("spring.cassandra.local-datacenter", "datacenter1"),
                environment.getProperty("spring.cassandra.keyspace-name", "distcomp")
        );
    }

    public static void ensureKeyspace(String contactPoints, int port, String datacenter, String keyspace) {
        if (keyspace == null || !keyspace.matches("[A-Za-z0-9_]+")) {
            throw new IllegalArgumentException("Invalid Cassandra keyspace name");
        }
        String host = contactPoints.split(",")[0].trim();
        RuntimeException lastFailure = null;
        for (int attempt = 1; attempt <= 5; attempt++) {
            try (CqlSession session = CqlSession.builder()
                    .addContactPoint(new InetSocketAddress(host, port))
                    .withLocalDatacenter(datacenter)
                    .build()) {
                session.execute("CREATE KEYSPACE IF NOT EXISTS " + keyspace
                        + " WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}");
                return;
            } catch (RuntimeException exception) {
                lastFailure = exception;
                try {
                    Thread.sleep(2_000L);
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                    throw exception;
                }
            }
        }
        throw lastFailure;
    }
}
