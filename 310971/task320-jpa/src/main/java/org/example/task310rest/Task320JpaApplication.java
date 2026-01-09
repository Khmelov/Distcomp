package org.example.task310rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("org.example.task310rest.model")
@EnableJpaRepositories("org.example.task310rest.repository")
public class Task320JpaApplication {

    public static void main(String[] args) {
        SpringApplication.run(Task320JpaApplication.class, args);
    }
}


