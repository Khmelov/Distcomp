package com.example.task310.service;

import com.example.task310.dto.*;
import com.example.task310.enums.PostState;
import com.example.task310.repository.IssueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final RestClient restClient;
    private final KafkaTemplate<String, PostRequestTo> kafkaTemplate;
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
        for (int i = 0; i < 3; i++) {
            try {
                return restClient.get()
                        .uri("/{id}", id)
                        .retrieve()
                        .body(PostResponseTo.class);
            } catch (HttpClientErrorException.NotFound e) {
                if (i == 2) throw new RuntimeException("Post not found");
                try { Thread.sleep(300); } catch (InterruptedException ignored) {}
            }
        }
        throw new RuntimeException("Post not found");
    }

    public PostResponseTo create(PostRequestTo dto) {
        if (!issueRepository.existsById(dto.getIssueId())) {
            throw new RuntimeException("Issue not found");
        }
        if (dto.getId() == null) {
            dto.setId(Math.abs(new java.util.Random().nextLong()));
        }
        dto.setState(PostState.PENDING);
        kafkaTemplate.send("InTopic", String.valueOf(dto.getIssueId()), dto);
        return new PostResponseTo(dto.getId(), dto.getIssueId(), dto.getContent(), dto.getState());
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