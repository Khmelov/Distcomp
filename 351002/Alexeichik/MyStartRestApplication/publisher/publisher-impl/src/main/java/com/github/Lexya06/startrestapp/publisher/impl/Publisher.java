package com.github.Lexya06.startrestapp.publisher.impl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class Publisher {
    public static void main(String[] args) {
        SpringApplication.run(Publisher.class, args);
    }
}
