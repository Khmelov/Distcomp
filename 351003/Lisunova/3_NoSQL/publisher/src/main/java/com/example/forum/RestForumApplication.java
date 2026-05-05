package com.example.forum;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

import java.util.Arrays;

@SpringBootApplication
public class RestForumApplication {
    @Bean
    public RestClient postClient() {
        return RestClient.builder()
                .baseUrl("http://localhost:24130/api/v1.0/posts")
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(RestForumApplication.class, args);
    }
}

