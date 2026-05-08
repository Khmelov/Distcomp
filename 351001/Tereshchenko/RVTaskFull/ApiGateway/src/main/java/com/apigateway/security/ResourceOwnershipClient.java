package com.apigateway.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ResourceOwnershipClient {

    private final WebClient tweetClient;
    private final WebClient messageClient;

    public ResourceOwnershipClient(
            WebClient.Builder webClientBuilder,
            @Value("${app.security.services.tweet-url}") String tweetServiceUrl,
            @Value("${app.security.services.message-url}") String messageServiceUrl) {
        this.tweetClient = webClientBuilder.baseUrl(tweetServiceUrl).build();
        this.messageClient = webClientBuilder.baseUrl(messageServiceUrl).build();
    }

    public Mono<Long> findTweetWriterId(Long tweetId) {
        return tweetClient.get()
                .uri("/api/v1.0/tweets/{id}", tweetId)
                .retrieve()
                .bodyToMono(TweetOwnerResponse.class)
                .map(TweetOwnerResponse::getWriterId);
    }

    public Mono<Long> findMessageTweetId(Long messageId) {
        return messageClient.get()
                .uri("/api/v1.0/messages/{id}", messageId)
                .retrieve()
                .bodyToMono(MessageOwnerResponse.class)
                .map(MessageOwnerResponse::getTweetId);
    }

    public static class TweetOwnerResponse {
        private Long writerId;

        public Long getWriterId() {
            return writerId;
        }

        public void setWriterId(Long writerId) {
            this.writerId = writerId;
        }
    }

    public static class MessageOwnerResponse {
        private Long tweetId;

        public Long getTweetId() {
            return tweetId;
        }

        public void setTweetId(Long tweetId) {
            this.tweetId = tweetId;
        }
    }
}
