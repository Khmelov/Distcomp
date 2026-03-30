package com.example.demo.service;

import com.example.demo.dto.requests.MessageRequestTo;
import com.example.demo.dto.responses.MessageResponseTo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageClientService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public MessageResponseTo create(MessageRequestTo dto) {
        try {
            String json = new ObjectMapper().writeValueAsString(dto);
            return webClient.post()
                    .uri("/messages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(json)
                    .retrieve()
                    .bodyToMono(MessageResponseTo.class)
                    .block();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize DTO", e);
        }
    }

    public MessageResponseTo findById(Long id) {
        return webClient.get()
                .uri("/messages/{id}", id)
                .retrieve()
                .bodyToMono(MessageResponseTo.class)
                .block();
    }

    public Page<MessageResponseTo> findAll(Pageable pageable, String contentFilter, Long issueIdFilter) {
        int pageNumber = pageable.isPaged() ? pageable.getPageNumber() : 0;
        int pageSize = pageable.isPaged() ? pageable.getPageSize() : 100;


        String sortParam;
        if (pageable.getSort().isSorted()) {
            sortParam = pageable.getSort().stream()
                    .map(order -> order.getProperty() + "," + (order.isAscending() ? "asc" : "desc"))
                    .collect(Collectors.joining(","));
        } else {
            sortParam = "id,asc";
        }

        Map<String, Object> response = webClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/messages");
                    uriBuilder.queryParam("page", pageNumber);
                    uriBuilder.queryParam("size", pageSize);
                    uriBuilder.queryParam("sort", sortParam);
                    if (issueIdFilter != null) {
                        uriBuilder.queryParam("issueId", issueIdFilter);
                    }
                    if (contentFilter != null && !contentFilter.isEmpty()) {
                        uriBuilder.queryParam("content", contentFilter);
                    }
                    return uriBuilder.build();
                })
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        if (response == null || !response.containsKey("content")) {
            return Page.empty(pageable);
        }

        List<MessageResponseTo> contentList = objectMapper.convertValue(
                response.get("content"),
                new TypeReference<List<MessageResponseTo>>() {}
        );

        int totalPages = response.containsKey("totalPages") ? (int) response.get("totalPages") : 1;
        long totalElements = response.containsKey("totalElements") ?
                ((Number) response.get("totalElements")).longValue() : contentList.size();

        return new PageImpl<>(contentList, pageable, totalElements);
    }

    private Comparator<MessageResponseTo> getComparatorForField(String field) {
        return switch (field) {
            case "id" -> Comparator.comparing(MessageResponseTo::getId);
            case "content" -> Comparator.comparing(MessageResponseTo::getContent, Comparator.nullsLast(String::compareTo));
            case "issueId" -> Comparator.comparing(MessageResponseTo::getIssueId);
            default -> (a, b) -> 0;
        };
    }

    public MessageResponseTo update(Long id, MessageRequestTo dto) {
        MessageResponseTo existing = findById(id);
        Long issueId = existing.getIssueId();
        dto.setIssueId(issueId);
        return webClient.put()
                .uri("/messages/{issueId}/{id}", issueId, id)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(MessageResponseTo.class)
                .block();
    }

    public void delete(Long id) {
        MessageResponseTo existing = findById(id);
        Long issueId = existing.getIssueId();
        webClient.delete()
                .uri("/messages/{issueId}/{id}", issueId, id)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
