package com.distcomp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the "Distcomp" REST application.
 *
 * <p>Task 310: REST architectural style over the {@code Creator}, {@code Issue},
 * {@code Tag} and {@code Notice} entities with in-memory storage.</p>
 */
@SpringBootApplication
public class DistcompApplication {

    public static void main(String[] args) {
        SpringApplication.run(DistcompApplication.class, args);
    }
}
