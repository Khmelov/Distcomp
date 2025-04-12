package by.molchan.Publisher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@SpringBootApplication
public class PublisherApplication {
	public static void main(String[] args) {
		SpringApplication.run(PublisherApplication.class, args);
	}

	@Bean
	public RestClient restClient(RestClient.Builder builder) {
		return builder
				.baseUrl("http://localhost:24130/api/v1.0")
				.build();
	}
}
