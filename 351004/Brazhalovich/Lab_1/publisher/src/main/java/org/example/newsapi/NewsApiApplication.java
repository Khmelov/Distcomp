package org.example.newsapi;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class NewsApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(NewsApiApplication.class, args);
    }
    @Bean
    public CommandLineRunner testDiscussion(WebClient.Builder webClientBuilder) {
        return args -> {
            WebClient client = webClientBuilder.baseUrl("http://localhost:24130").build();
            try {
                String result = client.get()
                        .uri("/api/v1.0/comments")
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
                System.out.println("Connection OK, response: " + result);
            } catch (Exception e) {
                System.err.println("Connection FAILED: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }

}