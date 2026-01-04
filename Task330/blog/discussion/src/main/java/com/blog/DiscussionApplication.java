package com.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.blog"})
public class DiscussionApplication {
    public static void main(String[] args) {
        SpringApplication.run(DiscussionApplication.class, args);
    }
}