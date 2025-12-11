package com.task.rest.service;

import com.task.rest.dto.CommentRequestTo;
import com.task.rest.dto.CommentResponseTo;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class DiscussionClient {

    private final WebClient webClient;

    public DiscussionClient(WebClient discussionWebClient) {
        this.webClient = discussionWebClient;
    }

    public CommentResponseTo createComment(CommentRequestTo dto) {
        return webClient.post()
                .uri("/comments")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(CommentResponseTo.class)
                .block();
    }

    public CommentResponseTo getComment(Long id) {
        return webClient.get()
                .uri("/comments/{id}", id)
                .retrieve()
                .bodyToMono(CommentResponseTo.class)
                .block();
    }

    public List<CommentResponseTo> getAllComments() {
        return webClient.get()
                .uri("/comments")
                .retrieve()
                .bodyToFlux(CommentResponseTo.class)
                .collectList()
                .block();
    }

    public List<CommentResponseTo> getCommentsByCountryAndTweet(String country, Long tweetId) {
        return webClient.get()
                .uri("/comments/{country}/{tweetId}", country, tweetId)
                .retrieve()
                .bodyToFlux(CommentResponseTo.class)
                .collectList()
                .block();
    }

    public List<CommentResponseTo> getCommentsByTweetId(Long tweetId) {
        return webClient.get()
                .uri("/comments/by-tweet/{tweetId}", tweetId)
                .retrieve()
                .bodyToFlux(CommentResponseTo.class)
                .collectList()
                .block();
    }

    public CommentResponseTo updateComment(Long id, CommentRequestTo dto) {
        return webClient.put()
                .uri("/comments/{id}", id)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(CommentResponseTo.class)
                .block();
    }

    public void deleteComment(Long id) {
        webClient.delete()
                .uri("/comments/{id}", id)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}