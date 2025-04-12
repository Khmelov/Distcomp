package com.example.rest.client;


import com.example.rest.dto.requestDto.MessageRequestTo;
import com.example.rest.dto.responseDto.MessageResponseTo;
import com.example.rest.dto.updateDto.MessageUpdateTo;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class DiscussionClient {
    private final RestClient restClient = RestClient.create("http://localhost:24130");

    public MessageResponseTo createMessage(MessageRequestTo request) {
        return restClient.post()
                .uri("/api/v1.0/messages")
                .body(request)
                .retrieve()
                .body(MessageResponseTo.class);
    }

    public List getAllMessages() {
        return restClient.get()
                .uri("/api/v1.0/messages")
                .retrieve()
                .body(List.class);
    }

    public MessageResponseTo getMessageById(Long id) {
        return restClient.get()
                .uri("/api/v1.0/messages/{id}", id)
                .retrieve()
                .body(MessageResponseTo.class);
    }

    public MessageResponseTo updateMessage(MessageUpdateTo request) {
        return restClient.put()
                .uri("/api/v1.0/messages")
                .body(request)
                .retrieve()
                .body(MessageResponseTo.class);
    }

    public boolean deleteMessage(long id) {
         restClient.delete()
                .uri("/api/v1.0/messages/{id}", id)
                .retrieve()
                .body(Boolean.class);
        //TODO not ot return constant
        return true;
    }
}
