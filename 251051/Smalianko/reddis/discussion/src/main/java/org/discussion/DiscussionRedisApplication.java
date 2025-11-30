package org.discussion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@SpringBootApplication(
        exclude = {CassandraAutoConfiguration.class}
)
public class DiscussionRedisApplication {
    public static void main(String[] args) {
        SpringApplication.run(DiscussionRedisApplication.class, args);
    }
}