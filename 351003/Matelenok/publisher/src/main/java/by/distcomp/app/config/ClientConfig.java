package by.distcomp.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfig {

    @Bean
    public RestClient discussionClient() {
        return RestClient.builder()
                .baseUrl("http://localhost:24130/api/v1.0/notes")
                .build();
    }
}