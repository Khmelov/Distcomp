package com.distcomp.discussion.config;

import com.datastax.oss.driver.api.core.CqlSession;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

/**
 * Builds the Cassandra session explicitly.
 *
 * <p>The keyspace must exist before the driver applies {@code USE distcomp}.
 * Spring Boot creates the session from {@code spring.cassandra.keyspace-name},
 * so a first start against an empty Cassandra would fail. The bootstrap session
 * below creates the keyspace first, then the real session is opened with it.</p>
 */
@Configuration
public class CassandraConfig {

    @Bean(destroyMethod = "close")
    public CqlSession cqlSession(CassandraProperties properties) {
        String host = properties.getContactPoints().isEmpty()
                ? "localhost"
                : properties.getContactPoints().get(0);
        int port = properties.getPort();
        String datacenter = properties.getLocalDatacenter();
        String keyspace = properties.getKeyspaceName();

        try (CqlSession bootstrap = CqlSession.builder()
                .addContactPoint(new InetSocketAddress(host, port))
                .withLocalDatacenter(datacenter)
                .build()) {
            bootstrap.execute("""
                    CREATE KEYSPACE IF NOT EXISTS %s
                    WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}
                    """.formatted(keyspace));
        }

        return CqlSession.builder()
                .addContactPoint(new InetSocketAddress(host, port))
                .withLocalDatacenter(datacenter)
                .withKeyspace(keyspace)
                .build();
    }
}
