package com.example.task310.service;

import com.example.task310.dto.*;
import com.example.task310.repository.IssueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final RestClient restClient;
    private final IssueRepository issueRepository;

    public List<PostResponseTo> getAll(Pageable pageable) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("page", pageable.getPageNumber())
                        .queryParam("size", pageable.getPageSize())
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<PostResponseTo>>() {});
    }

    public PostResponseTo getById(Long id) {
        return restClient.get()
                .uri("/{id}", id)
                .retrieve()
                .body(PostResponseTo.class);
    }

    public PostResponseTo create(PostRequestTo dto) {
        if (!issueRepository.existsById(dto.getIssueId())) {
            throw new RuntimeException("Issue not found in Postgres!");
        }
        return restClient.post()
                .body(dto)
                .retrieve()
                .body(PostResponseTo.class);
    }

    public PostResponseTo update(PostRequestTo dto) {
        return restClient.put()
                .uri("/{id}", dto.getId())
                .body(dto)
                .retrieve()
                .body(PostResponseTo.class);
    }

    public void delete(Long id) {
        restClient.delete()
                .uri("/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }
}