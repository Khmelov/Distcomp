package com.sergey.orsik.discussion.config;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Component
public class CassandraSchemaInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(CassandraSchemaInitializer.class);

    private final String keyspaceName;
    private final String contactPoints;
    private final int port;
    private final String localDatacenter;

    public CassandraSchemaInitializer(
            @Value("${spring.cassandra.keyspace-name:distcomp}") String keyspaceName,
            @Value("${spring.cassandra.contact-points:localhost}") String contactPoints,
            @Value("${spring.cassandra.port:9042}") int port,
            @Value("${spring.cassandra.local-datacenter:datacenter1}") String localDatacenter) {
        this.keyspaceName = keyspaceName;
        this.contactPoints = contactPoints;
        this.port = port;
        this.localDatacenter = localDatacenter;
    }

    @Override
    public void run(ApplicationArguments args) {
        try (CqlSession session = buildSession()) {
            session.execute("""
                    CREATE KEYSPACE IF NOT EXISTS %s
                    WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}
                    """.formatted(keyspaceName));
            session.execute("""
                    CREATE TABLE IF NOT EXISTS %s.tbl_comment_by_id (
                        id bigint PRIMARY KEY,
                        tweet_id bigint,
                        creator_id bigint,
                        content text,
                        created timestamp,
                        state text
                    )
                    """.formatted(keyspaceName));
            addColumnIfMissing(session, keyspaceName, "tbl_comment_by_id", "state", "text");
            addColumnIfMissing(session, keyspaceName, "tbl_comment_by_id", "creator_id", "bigint");
            session.execute("""
                    CREATE TABLE IF NOT EXISTS %s.tbl_comment_by_tweet (
                        tweet_id bigint,
                        created timestamp,
                        id bigint,
                        creator_id bigint,
                        content text,
                        state text,
                        PRIMARY KEY ((tweet_id), created, id)
                    ) WITH CLUSTERING ORDER BY (created DESC, id DESC)
                    """.formatted(keyspaceName));
            addColumnIfMissing(session, keyspaceName, "tbl_comment_by_tweet", "state", "text");
            addColumnIfMissing(session, keyspaceName, "tbl_comment_by_tweet", "creator_id", "bigint");
            log.info("Ensured Cassandra schema exists for keyspace '{}'", keyspaceName);
        }
    }

    private CqlSession buildSession() {
        CqlSessionBuilder builder = CqlSession.builder().withLocalDatacenter(localDatacenter);
        for (String contactPoint : contactPoints.split(",")) {
            builder.addContactPoint(new InetSocketAddress(contactPoint.trim(), port));
        }
        return builder.build();
    }

    private static void addColumnIfMissing(CqlSession session, String keyspace, String table, String column, String cqlType) {
        try {
            session.execute("ALTER TABLE %s.%s ADD %s %s".formatted(keyspace, table, column, cqlType));
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("conflicts with an existing column")) {
                return;
            }
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("already exists")) {
                return;
            }
            log.debug("ALTER {}.{} add {} skipped: {}", keyspace, table, column, e.getMessage());
        }
    }
}
