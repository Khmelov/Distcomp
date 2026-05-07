package com.example.news.client;

import com.example.common.dto.MessageResponseTo;
import com.example.common.dto.MessageRequestTo;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;

import java.util.List;

@Component
public class DiscussionClient {

    private final RestClient restClient;

    public DiscussionClient() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:24130/api/v1.0/messages")
                .build();
    }

    public List<MessageResponseTo> getMessagesByArticleId(Long articleId) {
        return restClient.get()
                .uri("/article/{articleId}", articleId)
                .retrieve()
                .body(new ParameterizedTypeReference<List<MessageResponseTo>>() {});
    }

    public MessageResponseTo createMessage(MessageRequestTo request) {
        return restClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(MessageResponseTo.class);
    }
}