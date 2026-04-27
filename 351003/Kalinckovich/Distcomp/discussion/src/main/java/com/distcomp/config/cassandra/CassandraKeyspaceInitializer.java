package com.distcomp.config.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.net.InetSocketAddress;
import java.time.Duration;

@Configuration
public class CassandraKeyspaceInitializer {

    @Value("${cassandra.contact-points:localhost}")
    private String contactPoints;

    @Value("${cassandra.port:9042}")
    private int port;

    @Value("${cassandra.keyspace:distcomp}")
    private String keyspace;

    @Value("${spring.data.cassandra.local-datacenter:datacenter1}")
    private String localDatacenter;

    @Value("${cassandra.username:}")
    private String username;

    @Value("${cassandra.password:}")
    private String password;

    @Bean
    public boolean createKeyspaceIfNotExists() {
        final int maxRetries = 5;
        final int retryDelayMillis = 3000;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                final CqlSessionBuilder builder = CqlSession.builder()
                        .addContactPoint(new InetSocketAddress(contactPoints, port))
                        .withLocalDatacenter(localDatacenter);

                if (!username.isEmpty()) {
                    builder.withAuthCredentials(username, password);
                }

                try (final CqlSession tempSession = builder.build()) {
                    tempSession.execute(
                            SimpleStatement.builder(
                                            "CREATE KEYSPACE IF NOT EXISTS " + keyspace +
                                                    " WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};"
                                    )
                                    .setTimeout(Duration.ofSeconds(10))
                                    .build()
                    );
                }
                return true;

            } catch (final Exception e) {
                if (attempt == maxRetries) {
                    throw new RuntimeException("Failed to create keyspace after " + maxRetries + " attempts", e);
                }
                try {
                    Thread.sleep(retryDelayMillis);
                } catch (final InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted while waiting to retry keyspace creation", ie);
                }
            }
        }
        return false;
    }

    @Bean
    @DependsOn("createKeyspaceIfNotExists")
    public CqlSession cqlSession() {
        final CqlSessionBuilder builder = CqlSession.builder()
                .addContactPoint(new InetSocketAddress(contactPoints, port))
                .withLocalDatacenter(localDatacenter)
                .withKeyspace(keyspace);

        if (!username.isEmpty()) {
            builder.withAuthCredentials(username, password);
        }

        return builder.build();
    }
}