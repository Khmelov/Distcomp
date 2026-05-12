package com.example.task340;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.task340")
@EnableCassandraRepositories(basePackages = "com.example.task340")
public class DiscussionApplication {
    public static void main(String[] args) {
        SpringApplication.run(DiscussionApplication.class, args);
    }
}