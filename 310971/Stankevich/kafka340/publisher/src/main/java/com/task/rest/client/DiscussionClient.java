package com.task.rest.client;

import com.task.rest.dto.NoticeRequestTo;
import com.task.rest.dto.NoticeResponseTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Slf4j
@Component
public class DiscussionClient {

    private final WebClient webClient;

    public DiscussionClient(@Value("${discussion.service.url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
        log.info("DiscussionClient initialized with baseUrl: {}", baseUrl);
    }

    public NoticeResponseTo createNotice(NoticeRequestTo request) {
        log.debug("Creating notice: {}", request);
        return webClient.post()
                .uri("/notices")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(NoticeResponseTo.class)
                .doOnError(e -> log.error("Error creating notice", e))
                .block();
    }

    public NoticeResponseTo getNoticeById(String country, Long tweetId, Long id) {
        log.debug("Getting notice by id: {}/{}/{}", country, tweetId, id);
        return webClient.get()
                .uri("/notices/{country}/{tweetId}/{id}", country, tweetId, id)
                .retrieve()
                .bodyToMono(NoticeResponseTo.class)
                .doOnError(e -> log.error("Error getting notice by id", e))
                .onErrorReturn(null)
                .block();
    }

    public List<NoticeResponseTo> getNoticesByTweetId(Long tweetId, String country) {
        log.debug("Getting notices by tweetId: {}, country: {}", tweetId, country);
        return webClient.get()
                .uri("/notices/tweet/{tweetId}?country={country}", tweetId, country)
                .retrieve()
                .bodyToFlux(NoticeResponseTo.class)
                .doOnError(e -> log.error("Error getting notices by tweetId", e))
                .collectList()
                .block();
    }

    public List<NoticeResponseTo> getAllNotices() {
        log.debug("Getting all notices");
        return webClient.get()
                .uri("/notices")
                .retrieve()
                .bodyToFlux(NoticeResponseTo.class)
                .doOnError(e -> log.error("Error getting all notices", e))
                .collectList()
                .block();
    }

    public NoticeResponseTo updateNotice(String country, Long tweetId, Long id, NoticeRequestTo request) {
        log.debug("Updating notice: {}/{}/{}", country, tweetId, id);
        return webClient.put()
                .uri("/notices/{country}/{tweetId}/{id}", country, tweetId, id)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(NoticeResponseTo.class)
                .doOnError(e -> log.error("Error updating notice", e))
                .block();
    }

    public void deleteNotice(String country, Long tweetId, Long id) {
        log.debug("Deleting notice: {}/{}/{}", country, tweetId, id);
        webClient.delete()
                .uri("/notices/{country}/{tweetId}/{id}", country, tweetId, id)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(e -> log.error("Error deleting notice", e))
                .block();
    }
}
