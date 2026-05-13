package com.sergey.orsik.discussion.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@Configuration
@EnableCassandraRepositories(basePackages = "com.sergey.orsik.discussion.repository")
public class CassandraRepositoriesConfig {
}
