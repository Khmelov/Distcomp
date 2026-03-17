package org.example.newsapi.service;

import lombok.RequiredArgsConstructor;
import org.example.newsapi.dto.request.CommentRequestTo;
import org.example.newsapi.dto.response.CommentResponseTo;
import org.example.newsapi.exception.NotFoundException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final WebClient discussionWebClient;

    public CommentResponseTo create(CommentRequestTo request) {
        return discussionWebClient.post()
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(String.class).flatMap(error -> {
                            if (response.statusCode().value() == 404) {
                                return Mono.error(new NotFoundException("Related news not found in discussion service"));
                            }
                            return Mono.error(new RuntimeException("Discussion service error: " + error));
                        }))
                .bodyToMono(CommentResponseTo.class)
                .block();
    }

    public List<CommentResponseTo> findAll() {
        return discussionWebClient.get()
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<CommentResponseTo>>() {})
                .block();
    }

    public CommentResponseTo findById(Long id) {
        return discussionWebClient.get()
                .uri("/{id}", id)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(String.class).flatMap(error -> {
                            if (response.statusCode().value() == 404) {
                                return Mono.error(new NotFoundException("Comment not found with id: " + id));
                            }
                            return Mono.error(new RuntimeException("Discussion service error: " + error));
                        }))
                .bodyToMono(CommentResponseTo.class)
                .block();
    }

    public CommentResponseTo update(Long id, CommentRequestTo request) {
        return discussionWebClient.put()
                .uri("/{id}", id)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(String.class).flatMap(error -> {
                            if (response.statusCode().value() == 404) {
                                return Mono.error(new NotFoundException("Comment not found with id: " + id));
                            }
                            return Mono.error(new RuntimeException("Discussion service error: " + error));
                        }))
                .bodyToMono(CommentResponseTo.class)
                .block();
    }

    public void delete(Long id) {
        discussionWebClient.delete()
                .uri("/{id}", id)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(String.class).flatMap(error -> {
                            if (response.statusCode().value() == 404) {
                                return Mono.error(new NotFoundException("Comment not found with id: " + id));
                            }
                            return Mono.error(new RuntimeException("Discussion service error: " + error));
                        }))
                .toBodilessEntity()
                .block();
    }
}