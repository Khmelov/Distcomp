package com.distcomp.config;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.net.InetSocketAddress;

@Configuration
public class CassandraKeyspaceInitializer {

    @Value("${cassandra.contact-points:localhost}")
    private String contactPoints;

    @Value("${cassandra.port:9042}")
    private int port;

    @Value("${cassandra.keyspace:distcomp}")
    private String keyspace;

    @Value("${cassandra.username:}")
    private String username;

    @Value("${cassandra.password:}")
    private String password;

    @Bean
    @Order(1)  
    public boolean createKeyspaceIfNotExists() {
        
        CqlSessionBuilder builder = CqlSession.builder()
                .addContactPoint(new InetSocketAddress(contactPoints, port))
                .withLocalDatacenter("datacenter1");  

        
        if (!username.isEmpty()) {
            builder = builder.withAuthCredentials(username, password);
        }

        try (final CqlSession session = builder.build()) {
            session.execute(String.format("CREATE KEYSPACE IF NOT EXISTS %s WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};",keyspace));
        }
        return true;  
    }
}