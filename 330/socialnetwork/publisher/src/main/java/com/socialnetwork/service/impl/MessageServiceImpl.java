package com.socialnetwork.service.impl;

import com.socialnetwork.dto.external.MessageRequestDto;
import com.socialnetwork.dto.external.MessageResponseDto;
import com.socialnetwork.dto.request.MessageRequestTo;
import com.socialnetwork.dto.response.MessageResponseTo;
import com.socialnetwork.mapper.MessageMapper;
import com.socialnetwork.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    private final WebClient webClient;
    private final MessageMapper messageMapper;

    @Autowired
    public MessageServiceImpl(WebClient discussionWebClient, MessageMapper messageMapper) {
        this.webClient = discussionWebClient;
        this.messageMapper = messageMapper;
    }

    @Override
    public List<MessageResponseTo> getAll() {
        List<MessageResponseDto> externalResponses = webClient.get()
                .uri("/api/v1.0/messages")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<MessageResponseDto>>() {})
                .onErrorResume(e -> {
                    System.err.println("Error getting all messages: " + e.getMessage());
                    return Mono.just(List.of());
                })
                .block();

        if (externalResponses == null) {
            return List.of();
        }

        return externalResponses.stream()
                .map(messageMapper::toResponse)
                .toList();
    }

    @Override
    public MessageResponseTo getById(Long id) {
        MessageResponseDto externalResponse = webClient.get()
                .uri("/api/v1.0/messages/{id}", id)
                .retrieve()
                .onStatus(status -> status.value() == 404,
                        response -> Mono.error(new RuntimeException("Message not found with id: " + id)))
                .bodyToMono(MessageResponseDto.class)
                .onErrorResume(e -> {
                    System.err.println("Error getting message by id " + id + ": " + e.getMessage());
                    return Mono.error(new RuntimeException("Message not found with id: " + id));
                })
                .block();

        return messageMapper.toResponse(externalResponse);
    }

    @Override
    public MessageResponseTo create(MessageRequestTo request) {
        MessageRequestDto externalRequest = messageMapper.toExternalRequest(request);

        MessageResponseDto externalResponse = webClient.post()
                .uri("/api/v1.0/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(externalRequest)
                .retrieve()
                .bodyToMono(MessageResponseDto.class)
                .onErrorResume(e -> {
                    System.err.println("Error creating message: " + e.getMessage());
                    return Mono.error(new RuntimeException("Failed to create message"));
                })
                .block();

        return messageMapper.toResponse(externalResponse);
    }

    @Override
    public MessageResponseTo update(Long id, MessageRequestTo request) {
        MessageRequestDto externalRequest = messageMapper.toExternalRequest(request);

        MessageResponseDto externalResponse = webClient.put()
                .uri("/api/v1.0/messages/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(externalRequest)
                .retrieve()
                .bodyToMono(MessageResponseDto.class)
                .onErrorResume(e -> {
                    System.err.println("Error updating message " + id + ": " + e.getMessage());
                    return Mono.error(new RuntimeException("Failed to update message with id: " + id));
                })
                .block();

        return messageMapper.toResponse(externalResponse);
    }

    @Override
    public void delete(Long id) {
        webClient.delete()
                .uri("/api/v1.0/messages/{id}", id)
                .retrieve()
                .toBodilessEntity()
                .onErrorResume(e -> {
                    System.err.println("Error deleting message " + id + ": " + e.getMessage());
                    return Mono.empty();
                })
                .block();
    }

    @Override
    public List<MessageResponseTo> getByTweetId(Long tweetId) {
        List<MessageResponseDto> externalResponses = webClient.get()
                .uri("/api/v1.0/messages/tweet/{tweetId}", tweetId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<MessageResponseDto>>() {})
                .onErrorResume(e -> {
                    System.err.println("Error getting messages by tweet id " + tweetId + ": " + e.getMessage());
                    return Mono.just(List.of());
                })
                .block();

        if (externalResponses == null) {
            return List.of();
        }

        return externalResponses.stream()
                .map(messageMapper::toResponse)
                .toList();
    }
}