package com.distcomp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.repository.config.EnableReactiveCassandraRepositories;

@Configuration
@EnableReactiveCassandraRepositories(basePackages = "com.distcomp.repository.cassandra")
public class CassandraRepositoryConfig {
}
