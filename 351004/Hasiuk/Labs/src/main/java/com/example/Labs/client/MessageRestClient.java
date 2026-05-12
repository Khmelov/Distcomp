package com.example.Labs.client;

import com.example.Labs.dto.request.MessageRequestTo;
import com.example.Labs.dto.response.MessageResponseTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;

@Component
public class MessageRestClient {

    private final WebClient webClient;

    @Autowired
    public MessageRestClient() {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:24130/api/v1.0/messages")
                .build();
    }

    public List<MessageResponseTo> getAll() {
        return webClient.get()
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<MessageResponseTo>>() {})
                .block();
    }

    public MessageResponseTo getById(Long id) {
        return webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(MessageResponseTo.class)
                .block();
    }

    public List<MessageResponseTo> getByStoryId(Long storyId) {
        return webClient.get()
                .uri("/by-story/{storyId}", storyId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<MessageResponseTo>>() {})
                .block();
    }

    public MessageResponseTo create(MessageRequestTo request) {
        return webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(MessageResponseTo.class)
                .block();
    }

    public MessageResponseTo update(Long id, MessageRequestTo request) {
        return webClient.put()
                .uri("/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(MessageResponseTo.class)
                .block();
    }

    public void delete(Long id) {
        webClient.delete()
                .uri("/{id}", id)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}