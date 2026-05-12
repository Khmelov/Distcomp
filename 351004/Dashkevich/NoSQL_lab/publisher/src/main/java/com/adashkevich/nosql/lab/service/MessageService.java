package com.adashkevich.nosql.lab.service;

import com.adashkevich.nosql.lab.dto.request.MessageRequestTo;
import com.adashkevich.nosql.lab.dto.response.MessageResponseTo;
import com.adashkevich.nosql.lab.exception.NotFoundException;
import com.adashkevich.nosql.lab.exception.ValidationException;
import com.adashkevich.nosql.lab.repository.NewsRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class MessageService {

    private final RestClient discussionClient;
    private final NewsRepository newsRepo;

    public MessageService(RestClient.Builder restClientBuilder, NewsRepository newsRepo) {
        this.discussionClient = restClientBuilder
                .baseUrl("http://localhost:24130/api/v1.0")
                .build();
        this.newsRepo = newsRepo;
    }

    public MessageResponseTo create(MessageRequestTo dto) {
        ensureNewsExists(dto.newsId);

        return discussionClient.post()
                .uri("/messages")
                .body(dto)
                .retrieve()
                .body(MessageResponseTo.class);
    }

    public List<MessageResponseTo> getAll() {
        return discussionClient.get()
                .uri("/messages")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public MessageResponseTo getById(Long id) {
        return discussionClient.get()
                .uri("/messages/{id}", id)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        (request, response) -> {
                            throw new NotFoundException("Message not found", "40440");
                        }
                )
                .body(MessageResponseTo.class);
    }

    public MessageResponseTo update(Long id, MessageRequestTo dto) {
        ensureNewsExists(dto.newsId);

        return discussionClient.put()
                .uri("/messages/{id}", id)
                .body(dto)
                .retrieve()
                .body(MessageResponseTo.class);
    }

    public void delete(Long id) {
        discussionClient.delete()
                .uri("/messages/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }

    public List<MessageResponseTo> getByNewsId(Long newsId) {
        ensureNewsExists(newsId);

        return discussionClient.get()
                .uri("/messages/by-news/{newsId}", newsId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public void deleteByNewsId(Long newsId) {
        discussionClient.delete()
                .uri("/messages/by-news/{newsId}", newsId)
                .retrieve()
                .toBodilessEntity();
    }

    private void ensureNewsExists(Long newsId) {
        if (newsId == null || !newsRepo.existsById(newsId)) {
            throw new ValidationException("newsId does not exist", "40020");
        }
    }
}