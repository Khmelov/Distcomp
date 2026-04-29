package by.bsuir.distcomp.discussion.config;

import com.datastax.oss.driver.api.core.CqlSession;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetSocketAddress;

/**
 * Создаёт keyspace до {@code CqlSession}: иначе Spring падает с Invalid keyspace, потому что
 * сессия поднимается раньше, чем отрабатывает Liquibase.
 */
public class CassandraKeyspaceInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        Environment env = context.getEnvironment();
        String points = env.getProperty("spring.cassandra.contact-points", "127.0.0.1");
        String host = points.split(",")[0].trim();
        int port = env.getProperty("spring.cassandra.port", Integer.class, 9042);
        String dc = env.getProperty("spring.cassandra.local-datacenter", "datacenter1");

        try (CqlSession session = CqlSession.builder()
                .addContactPoint(new InetSocketAddress(host, port))
                .withLocalDatacenter(dc)
                .build()) {
            session.execute(
                    "CREATE KEYSPACE IF NOT EXISTS distcomp WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}");
        }
    }
}
