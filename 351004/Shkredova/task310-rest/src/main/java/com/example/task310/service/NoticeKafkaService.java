package com.example.task310.service;

import com.example.task310.dto.kafka.NoticeMessage;
import com.example.task310.exception.ValidationException;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@SuppressWarnings("unchecked")
public class NoticeKafkaService {
    private final ReplyingKafkaTemplate<String, NoticeMessage, NoticeMessage> replyingTemplate;

    public NoticeKafkaService(ReplyingKafkaTemplate<String, NoticeMessage, NoticeMessage> replyingTemplate) {
        this.replyingTemplate = replyingTemplate;
    }

    public NoticeMessage sendForModeration(Long newsId, String content) {
        String correlationId = UUID.randomUUID().toString();
        NoticeMessage message = new NoticeMessage();
        message.setNewsId(newsId);
        message.setContent(content);
        message.setState("PENDING");
        message.setCorrelationId(correlationId);

        Message<NoticeMessage> requestMessage = MessageBuilder
                .withPayload(message)
                .setHeader(KafkaHeaders.TOPIC, "InTopic")
                .setHeader(KafkaHeaders.KEY, newsId.toString())
                .setHeader(KafkaHeaders.CORRELATION_ID, correlationId.getBytes(StandardCharsets.UTF_8))
                .build();

        try {
            // Исправление: явное приведение типа
            var future = replyingTemplate.sendAndReceive(requestMessage);
            Message<?> responseMessage = future.get(5, TimeUnit.SECONDS);

            if (responseMessage != null && responseMessage.getPayload() != null) {
                // Приведение payload к NoticeMessage
                Object payload = responseMessage.getPayload();
                if (payload instanceof NoticeMessage) {
                    return (NoticeMessage) payload;
                }
            }
            throw new ValidationException("No valid response from moderation service");
        } catch (Exception e) {
            throw new ValidationException("Moderation service unavailable or timeout: " + e.getMessage());
        }
    }
}