package com.distcomp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.data.cassandra.autoconfigure.DataCassandraReactiveAutoConfiguration;

@SpringBootApplication(
        scanBasePackages = "com.distcomp")
public class NoteApplication {
    public static void main(final String[] args) {
        SpringApplication.run(NoteApplication.class, args);
    }
}