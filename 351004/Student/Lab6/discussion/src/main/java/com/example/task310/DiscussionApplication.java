package com.example.task310;

import com.example.task310.repository.PostRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;
import org.springframework.data.domain.PageRequest;

@SpringBootApplication
@EnableCassandraRepositories(basePackages = "com.example.task310.repository")
@RequiredArgsConstructor
public class DiscussionApplication {

	private final PostRepository repository;

	public static void main(String[] args) {
		SpringApplication.run(DiscussionApplication.class, args);
	}

	@PostConstruct
	public void warmup() {
		try {
			System.out.println("Warming up Cassandra connection...");
			repository.findAll(PageRequest.of(0, 1));
			System.out.println("Cassandra is ready!");
		} catch (Exception e) {
			System.err.println("Warmup failed: " + e.getMessage());
		}
	}
}