package com.example.discussion.config;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
public class CassandraInitConfig {

    @Bean
    CqlSession cqlSession(CassandraProperties properties) {
        List<InetSocketAddress> contactPoints = properties.getContactPoints().stream()
                .map(host -> new InetSocketAddress(host, properties.getPort()))
                .toList();

        CqlSessionBuilder adminBuilder = CqlSession.builder()
                .withLocalDatacenter(properties.getLocalDatacenter());
        contactPoints.forEach(adminBuilder::addContactPoint);

        if (properties.getUsername() != null && properties.getPassword() != null) {
            adminBuilder.withAuthCredentials(properties.getUsername(), properties.getPassword());
        }

        try (CqlSession adminSession = adminBuilder.build()) {
            adminSession.execute(SimpleStatement.newInstance(
                    "CREATE KEYSPACE IF NOT EXISTS " + properties.getKeyspaceName()
                            + " WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}"
            ));
        }

        CqlSessionBuilder appBuilder = CqlSession.builder()
                .withLocalDatacenter(properties.getLocalDatacenter())
                .withKeyspace(CqlIdentifier.fromCql(properties.getKeyspaceName()));
        contactPoints.forEach(appBuilder::addContactPoint);

        if (properties.getUsername() != null && properties.getPassword() != null) {
            appBuilder.withAuthCredentials(properties.getUsername(), properties.getPassword());
        }

        return appBuilder.build();
    }

    @Bean
    CommandLineRunner initDiscussionSchema(
            CqlSession cqlSession,
            @Value("${discussion.db.init-cql}") Resource initCql) {
        return args -> {
            String cqlScript = new String(FileCopyUtils.copyToByteArray(initCql.getInputStream()), StandardCharsets.UTF_8);
            String[] statements = cqlScript.split(";");
            for (String statement : statements) {
                String trimmed = statement.trim();
                if (!trimmed.isEmpty()) {
                    cqlSession.execute(trimmed);
                }
            }
        };
    }
}
