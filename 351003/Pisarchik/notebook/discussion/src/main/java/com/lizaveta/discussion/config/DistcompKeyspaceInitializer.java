package com.lizaveta.discussion.config;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Component
public class DistcompKeyspaceInitializer implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(DistcompKeyspaceInitializer.class);

    private final String contactPoints;
    private final int port;
    private final String localDatacenter;
    private final String keyspaceName;

    public DistcompKeyspaceInitializer(
            @Value("${spring.cassandra.contact-points:127.0.0.1}") final String contactPoints,
            @Value("${spring.cassandra.port:9042}") final int port,
            @Value("${spring.cassandra.local-datacenter:datacenter1}") final String localDatacenter,
            @Value("${spring.cassandra.keyspace-name:distcomp}") final String keyspaceName) {
        this.contactPoints = contactPoints;
        this.port = port;
        this.localDatacenter = localDatacenter;
        this.keyspaceName = keyspaceName;
    }

    @Override
    public void afterPropertiesSet() {
        if (!isSimpleKeyspaceName(keyspaceName)) {
            throw new IllegalArgumentException("Refusing unsafe keyspace name: " + keyspaceName);
        }
        CqlSessionBuilder builder = CqlSession.builder().withLocalDatacenter(localDatacenter);
        for (String host : contactPoints.split(",")) {
            String trimmed = host.trim();
            if (!trimmed.isEmpty()) {
                builder.addContactPoint(new InetSocketAddress(trimmed, port));
            }
        }
        try (CqlSession session = builder.build()) {
            String cql = "CREATE KEYSPACE IF NOT EXISTS " + keyspaceName
                    + " WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}";
            session.execute(SimpleStatement.newInstance(cql));
            log.info("Cassandra keyspace ready: {}", keyspaceName);
        }
    }

    private static boolean isSimpleKeyspaceName(final String name) {
        return name != null && !name.isEmpty() && name.chars().allMatch(Character::isLetterOrDigit);
    }
}
