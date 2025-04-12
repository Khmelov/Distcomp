package by.kopvzakone.distcomp;

import by.kopvzakone.distcomp.repositories.Repo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class DistCompApplication {

	public static void main(String[] args) {
		SpringApplication.run(DistCompApplication.class, args);
	}
	@Bean
	public WebClient webClient() {
		return WebClient.builder().baseUrl("http://localhost:24130").build();
	}
}
