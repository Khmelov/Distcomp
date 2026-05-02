package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@Configuration
@Profile("cassandra")
@EnableCassandraRepositories(basePackages = "com.example.demo.cassandra.repository")
public class CassandraConfig {
}