package com.example.task310.service;

import com.example.task310.dto.NoticeRequestTo;
import com.example.task310.dto.NoticeResponseTo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class NoticeRestClientService {
    private final RestClient restClient;

    public NoticeRestClientService(@Value("${discussion.base-url:http://localhost:24130}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public List<NoticeResponseTo> findAll() {
        return restClient.get()
                .uri("/api/v1.0/notices")
                .retrieve()
                .body(new ParameterizedTypeReference<List<NoticeResponseTo>>() {});
    }

    public NoticeResponseTo findById(Long id) {
        try {
            return restClient.get()
                    .uri("/api/v1.0/notices/{id}", id)
                    .retrieve()
                    .body(NoticeResponseTo.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notice not found");
        }
    }

    public NoticeResponseTo create(NoticeRequestTo request) {
        return restClient.post()
                .uri("/api/v1.0/notices")
                .body(request)
                .retrieve()
                .body(NoticeResponseTo.class);
    }

    public NoticeResponseTo update(NoticeRequestTo request) {
        return restClient.put()
                .uri("/api/v1.0/notices")
                .body(request)
                .retrieve()
                .body(NoticeResponseTo.class);
    }

    public void delete(Long id) {
        restClient.delete()
                .uri("/api/v1.0/notices/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }
}