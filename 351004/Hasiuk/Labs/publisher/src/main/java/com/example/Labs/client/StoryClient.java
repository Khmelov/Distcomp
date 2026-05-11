package com.example.Labs.client;

import com.example.Labs.dto.response.StoryResponseTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class StoryClient {

    private final WebClient webClient;

    @Autowired
    public StoryClient() {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:24110/api/v1.0/stories")
                .build();
    }

    public boolean existsById(Long id) {
        try {
            StoryResponseTo story = webClient.get()
                    .uri("/{id}", id)
                    .retrieve()
                    .bodyToMono(StoryResponseTo.class)
                    .block();
            return story != null && story.getId() != null;
        } catch (WebClientResponseException.NotFound e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}