package com.distcomp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the "Distcomp" application.
 *
 * <p>Task 320: the REST layer of task 310 backed by a PostgreSQL storage
 * implemented with JPA / Spring Data, with a Liquibase database schema.</p>
 */
@SpringBootApplication
public class DistcompApplication {

    public static void main(String[] args) {
        SpringApplication.run(DistcompApplication.class, args);
    }
}
