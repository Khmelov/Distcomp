package com.lizaveta.notebook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.lizaveta.notebook.repository.jpa")
public class NotebookApplication {

    public static void main(final String[] args) {
        SpringApplication.run(NotebookApplication.class, args);
    }
}
