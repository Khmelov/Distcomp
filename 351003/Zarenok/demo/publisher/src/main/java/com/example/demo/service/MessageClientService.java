package com.example.demo.service;

import com.example.demo.dto.requests.MessageRequestTo;
import com.example.demo.dto.responses.MessageResponseTo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageClientService {

    private final WebClient webClient;

    public MessageResponseTo create(MessageRequestTo dto) {
        return webClient.post()
                .uri("/messages")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(MessageResponseTo.class)
                .block();
    }

    public MessageResponseTo findById(Long id) {
        return webClient.get()
                .uri("/messages/{id}", id)
                .retrieve()
                .bodyToMono(MessageResponseTo.class)
                .block();
    }

    public Page<MessageResponseTo> findAll(Pageable pageable, String contentFilter, Long issueIdFilter) {
        List<MessageResponseTo> allMessages;
        if (issueIdFilter != null) {
            allMessages = webClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/messages").queryParam("issueId", issueIdFilter).build())
                    .retrieve()
                    .bodyToFlux(MessageResponseTo.class)
                    .collectList()
                    .block();
        } else {
            allMessages = webClient.get()
                    .uri("/messages")
                    .retrieve()
                    .bodyToFlux(MessageResponseTo.class)
                    .collectList()
                    .block();
        }

        if (contentFilter != null && !contentFilter.isEmpty()) {
            allMessages = allMessages.stream()
                    .filter(msg -> msg.getContent() != null && msg.getContent().contains(contentFilter))
                    .collect(Collectors.toList());
        }

        if (pageable.getSort().isSorted()) {
            Comparator<MessageResponseTo> comparator = null;
            for (Sort.Order order : pageable.getSort()) {
                Comparator<MessageResponseTo> fieldComparator = getComparatorForField(order.getProperty());
                if (order.isDescending()) {
                    fieldComparator = fieldComparator.reversed();
                }
                comparator = (comparator == null) ? fieldComparator : comparator.thenComparing(fieldComparator);
            }
            if (comparator != null) {
                allMessages.sort(comparator);
            }
        }

        // Пагинация
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), allMessages.size());
        List<MessageResponseTo> pageContent = allMessages.subList(start, end);

        return new PageImpl<>(pageContent, pageable, allMessages.size());
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
