package com.example.lab.publisher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class LabPublisherApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(LabPublisherApplication.class, args);
	}
}

// docker exec -it cassandra cqlsh
// CREATE KEYSPACE distcomp WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};