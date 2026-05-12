package com.example.discussion.service;

import com.example.discussion.dto.request.MessageRequestTo;
import com.example.discussion.dto.response.MessageResponseTo;
import com.example.discussion.entity.MessageState;
import com.example.discussion.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaDiscussionListener {
    private final MessageService messageService;
    private final ObjectMapper objectMapper;

    // WebClient для проверки существования story в publisher
    private final WebClient storyWebClient = WebClient.builder()
            .baseUrl("http://localhost:24110/api/v1.0/stories")
            .build();

    private boolean storyExists(Long storyId) {
        try {
            storyWebClient.get()
                    .uri("/{id}", storyId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            return true;
        } catch (WebClientResponseException.NotFound e) {
            return false;
        } catch (Exception e) {
            log.warn("Could not verify storyId {}: {}", storyId, e.getMessage());
            return false;
        }
    }

    @KafkaListener(topics = "InTopic", groupId = "discussion-group-v3")
    @SendTo
    public String listen(ConsumerRecord<String, String> record) {
        try {
            String op = new String(record.headers().lastHeader("op").value(), StandardCharsets.UTF_8);
            byte[] idH = record.headers().lastHeader("id") != null
                    ? record.headers().lastHeader("id").value() : null;
            Long id = idH != null ? Long.parseLong(new String(idH, StandardCharsets.UTF_8)) : null;

            Object result = null;

            if ("CREATE".equals(op)) {
                MessageRequestTo req = objectMapper.readValue(record.value(), MessageRequestTo.class);

                // Проверяем существование story перед созданием
                if (!storyExists(req.getStoryId())) {
                    return "ERROR:404:Story not found: " + req.getStoryId();
                }

                MessageResponseTo resp = messageService.create(req);
                MessageState state = resp.getContent().toLowerCase().contains("spam")
                        ? MessageState.DECLINE : MessageState.APPROVE;
                result = messageService.updateState(resp.getStoryId(), resp.getId(), state);

            } else if ("GET_ALL".equals(op)) {
                result = messageService.getAll();

            } else if ("GET_BY_ID".equals(op)) {
                result = messageService.getById(id);

            } else if ("DELETE".equals(op)) {
                messageService.delete(id);
                result = "DELETED";

            } else if ("UPDATE".equals(op)) {
                MessageRequestTo req = objectMapper.readValue(record.value(), MessageRequestTo.class);
                result = messageService.update(id, req);
            }

            return result instanceof String
                    ? (String) result
                    : objectMapper.writeValueAsString(result);

        } catch (ResourceNotFoundException e) {
            return "ERROR:404:" + e.getMessage();
        } catch (Exception e) {
            log.error("Kafka processing error", e);
            return "ERROR:" + e.getMessage();
        }
    }
}