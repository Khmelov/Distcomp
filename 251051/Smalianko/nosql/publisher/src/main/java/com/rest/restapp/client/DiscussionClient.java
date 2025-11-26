package com.rest.restapp.client;

import com.rest.restapp.dto.request.NoticeRequestToDto;
import com.rest.restapp.dto.response.NoticeResponseToDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class DiscussionClient {

    private final WebClient webClient;

    public DiscussionClient() {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:24130/api/v1.0")
                .build();
    }

    public NoticeResponseToDto createNotice(NoticeRequestToDto request) {
        return webClient.post()
                .uri("/notices")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(NoticeResponseToDto.class)
                .block();
    }

    public List<NoticeResponseToDto> getAll() {
        return webClient.get()
                .uri("/notices")
                .retrieve()
                .bodyToFlux(NoticeResponseToDto.class)
                .collectList()
                .block();
    }

    public NoticeResponseToDto getById(Long id) {
        return webClient.get()
                .uri("/notices/{id}", id)
                .retrieve()
                .bodyToMono(NoticeResponseToDto.class)
                .block();
    }

    public NoticeResponseToDto update(Long id, NoticeRequestToDto request) {
        return webClient.put()
                .uri("/notices/{id}", id)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(NoticeResponseToDto.class)
                .block();
    }

    public List<NoticeResponseToDto> getNoticesByIssueId(Long issueId) {
        return webClient.get()
                .uri("/notices/issue/" + issueId)
                .retrieve()
                .bodyToFlux(NoticeResponseToDto.class)
                .collectList()
                .block();
    }
    
    public ResponseEntity<Void> deleteNotice(Long noticeId) {
        return webClient.delete()
                .uri("/notices/{id}", noticeId)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
