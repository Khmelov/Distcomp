package com.distcomp.discussion.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@Configuration
@EnableCassandraRepositories(basePackages = "com.distcomp.discussion.repository")
public class CassandraConfig {
}