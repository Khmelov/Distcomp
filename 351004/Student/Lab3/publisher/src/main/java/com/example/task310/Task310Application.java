package com.example.task310;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient; // ВАЖНЫЙ ИМПОРТ

@SpringBootApplication
public class Task310Application {

	public static void main(String[] args) {
		SpringApplication.run(Task310Application.class, args);
	}

	@Bean
	public RestClient restClient() {
		return RestClient.builder()
				.baseUrl("http://localhost:24130/api/v1.0/posts")
				.build();
	}
}