package com.messageservice.configs.cassandraconfig;

import com.datastax.oss.driver.api.core.CqlSession;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CassandraSchemaInitializer implements ApplicationRunner {

    private final CqlSession session;
    private final DiscussionCassandraProperties properties;

    @Override
    public void run(ApplicationArguments args) {
        String keyspace = properties.getKeyspace();

        session.execute("""
                CREATE KEYSPACE IF NOT EXISTS %s
                WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}
                """.formatted(keyspace));

        session.execute("""
                CREATE TABLE IF NOT EXISTS %s.tbl_message (
                    id bigint PRIMARY KEY,
                    tweet_id bigint,
                    bucket int,
                    content text
                )
                """.formatted(keyspace));

        session.execute("""
                CREATE TABLE IF NOT EXISTS %s.tbl_message_by_tweet (
                    tweet_id bigint,
                    bucket int,
                    id bigint,
                    content text,
                    PRIMARY KEY ((tweet_id, bucket), id)
                )
                """.formatted(keyspace));
    }
}
