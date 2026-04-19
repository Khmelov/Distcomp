package com.example.news.service;

import com.example.common.dto.MessageRequestTo;
import com.example.common.dto.MessageResponseTo;
import com.example.common.dto.model.enums.MessageState;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final RestTemplate restTemplate;
    private final String DISCUSSION_URL = "http://localhost:24130/api/v1.0/messages";
    private final KafkaTemplate<String, MessageResponseTo> kafkaTemplate;
    private static final String IN_TOPIC = "InTopic";

    // Создание сообщения через Kafka (Асинхронно)
    public MessageResponseTo create(MessageRequestTo request) {
        Long generatedId = System.currentTimeMillis();

        MessageResponseTo pendingMessage = new MessageResponseTo(
                generatedId,
                request.articleId(),
                request.content(),
                MessageState.PENDING
        );

        kafkaTemplate.send(IN_TOPIC, String.valueOf(request.articleId()), pendingMessage);

        return pendingMessage;
    }

    @KafkaListener(topics = "OutTopic", groupId = "publisher-group")
    public void listenOutTopic(@Payload MessageResponseTo messageDto) {
        System.out.println("Модерация завершена для сообщения ID: " + messageDto.id());
        System.out.println("Статус: " + messageDto.state());

        // Здесь можно добавить логику уведомления пользователя через WebSocket
        // или просто логирование для отладки
    }

    // Остальные методы пока остаются на REST, как требует задание (не отключать REST)
    public List<MessageResponseTo> findAll(int page, int size, String sortBy) {
        MessageResponseTo[] response = restTemplate.getForObject(DISCUSSION_URL, MessageResponseTo[].class);
        return response != null ? Arrays.asList(response) : List.of();
    }

    public MessageResponseTo findById(Long id) {
        return restTemplate.getForObject(DISCUSSION_URL + "/" + id, MessageResponseTo.class);
    }

    public void delete(Long id) {
        restTemplate.delete(DISCUSSION_URL + "/" + id);
    }

    public MessageResponseTo update(Long id, MessageRequestTo request) {
        restTemplate.put(DISCUSSION_URL + "/" + id, request);
        return findById(id);
    }
}