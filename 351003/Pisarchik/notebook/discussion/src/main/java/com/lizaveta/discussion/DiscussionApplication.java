package com.lizaveta.discussion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@SpringBootApplication
@EnableCassandraRepositories(basePackages = "com.lizaveta.discussion.repository")
public class DiscussionApplication {

    public static void main(final String[] args) {
        SpringApplication.run(DiscussionApplication.class, args);
    }
}
