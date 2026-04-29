package com.example.task310;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient; // ВАЖНЫЙ ИМПОРТ

import java.net.http.HttpClient;
import java.time.Duration;

@SpringBootApplication
public class Task310Application {

	public static void main(String[] args) {
		SpringApplication.run(Task310Application.class, args);
	}

	@Bean
	public RestClient restClient() {
		JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory();
		factory.setReadTimeout(Duration.ofSeconds(2));

		return RestClient.builder()
				.baseUrl("http://localhost:24130/api/v1.0/posts")
				.requestFactory(factory)
				.build();
	}
}