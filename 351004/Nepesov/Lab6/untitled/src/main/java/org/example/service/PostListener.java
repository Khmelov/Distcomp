package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.PostMessage;
import org.example.model.PostState;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostListener {

    private final PostService postService;
    private final KafkaTemplate<String, PostMessage> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "InTopic", groupId = "discussion-group")
    public void listen(String messageJson,
                       @Header(value = KafkaHeaders.CORRELATION_ID, required = false) byte[] correlationId) {

        log.info("Discussion: Received message from Kafka. CorrelationId: {}", (correlationId != null));

        try {
            // 1. Десериализация
            PostMessage message = objectMapper.readValue(messageJson, PostMessage.class);

            // 2. Логика обработки (спам-фильтр)
            if (message.getContent() != null && message.getContent().toLowerCase().contains("spam")) {
                message.setState(PostState.DECLINE);
            } else {
                message.setState(PostState.APPROVE);
            }

            // 3. Сохранение ЧЕРЕЗ СЕРВИС (чтобы сработал @CachePut)
            postService.updateFromKafka(message);
            log.info("Discussion: Database and Redis updated for ID: {}", message.getId());

            // 4. Формируем ответ в OutTopic
            var replyBuilder = MessageBuilder
                    .withPayload(message)
                    .setHeader(KafkaHeaders.TOPIC, "OutTopic")
                    .setHeader(KafkaHeaders.KEY, String.valueOf(message.getId()));


            if (correlationId != null) {
                replyBuilder.setHeader(KafkaHeaders.CORRELATION_ID, correlationId);
            }

            kafkaTemplate.send(replyBuilder.build());
            log.info("Discussion: Reply sent back to Kafka for ID: {}", message.getId());

        } catch (Exception e) {
            log.error("Discussion: Critical error in listener: {}", e.getMessage(), e);
        }
    }
}