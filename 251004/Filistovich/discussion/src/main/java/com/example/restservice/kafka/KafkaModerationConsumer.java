package com.example.restservice.kafka;

import com.example.restservice.dto.MessageCreateDto;
import com.example.restservice.mapper.MessageMapper;
import com.example.restservice.model.MessageEvent;
import com.example.restservice.model.Status;
import com.example.restservice.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KafkaModerationConsumer {
    private final MessageService messageService;
    private final MessageMapper messageMapper;
    private final KafkaTemplate<String, MessageEvent> kafkaTemplate;
    private final List<String> stopWords = List.of("spam", "scam");

    @KafkaListener(topics = "InTopic", groupId = "publisher-group", containerFactory = "kafkaListenerContainerFactory")
    public void moderateMessage(MessageEvent event) {
        switch (event.getType()) {
            case "create" ->
                    messageService.save(messageMapper.toEntity(new MessageCreateDto(event.getId(), event.getArticleId(), event.getContent())));
            case "update" -> {
                messageService.update(messageMapper.toEntity(new MessageCreateDto(event.getId(), event.getArticleId(), event.getContent())));
            }
            case "delete" -> {
                messageService.deleteById(event.getId());
                return;
            }
        }

        Status status = containsStopWords(event.getContent())
                ? Status.DECLINED
                : Status.APPROVED;
        messageService.updateState(event.getId(), status);

        event.setStatus(status);
        kafkaTemplate.send("OutTopic", event.getArticleId().toString(), event);
    }

    private boolean containsStopWords(String content) {
        return stopWords.stream().anyMatch(content::contains);
    }
}