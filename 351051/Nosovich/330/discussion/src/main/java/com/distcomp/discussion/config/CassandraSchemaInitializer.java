package com.distcomp.discussion.config;

import com.datastax.oss.driver.api.core.CqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Creates the Cassandra keyspace and the tables owned by the discussion module.
 *
 * <p>Liquibase has no first-class CQL support in the community edition. Keeping
 * the schema next to the application makes the startup deterministic and avoids
 * an extra extension in the build. The statements are idempotent, so they can be
 * executed on every start.</p>
 */
@Component
public class CassandraSchemaInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(CassandraSchemaInitializer.class);

    private final CqlSession session;

    public CassandraSchemaInitializer(CqlSession session) {
        this.session = session;
    }

    @Override
    public void run(ApplicationArguments args) {
        session.execute("""
                CREATE KEYSPACE IF NOT EXISTS distcomp
                WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}
                """);

        // Main table: one partition per issue, notices ordered by identifier.
        // country is intentionally absent: it was the skew source in the given schema.
        session.execute("""
                CREATE TABLE IF NOT EXISTS distcomp.tbl_notice (
                    issue_id bigint,
                    id bigint,
                    content text,
                    state text,
                    PRIMARY KEY ((issue_id), id)
                ) WITH CLUSTERING ORDER BY (id ASC)
                """);

        // Lookup table for GET/PUT/DELETE by identifier. Cassandra cannot query
        // a table by a non-key column without ALLOW FILTERING, so the same row is
        // stored under both access paths.
        session.execute("""
                CREATE TABLE IF NOT EXISTS distcomp.tbl_notice_by_id (
                    id bigint PRIMARY KEY,
                    issue_id bigint,
                    content text,
                    state text
                )
                """);

        // Single-row counter used to allocate globally unique notice identifiers.
        session.execute("""
                CREATE TABLE IF NOT EXISTS distcomp.tbl_notice_counter (
                    name text PRIMARY KEY,
                    value counter
                )
                """);
        session.execute("UPDATE distcomp.tbl_notice_counter SET value = value + 0 WHERE name = 'notice'");

        log.info("Cassandra schema 'distcomp' is ready");
    }
}
