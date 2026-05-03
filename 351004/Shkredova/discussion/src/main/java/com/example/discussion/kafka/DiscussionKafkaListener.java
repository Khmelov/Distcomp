package com.example.discussion.kafka;

import com.example.discussion.dto.NoticeMessage;
import com.example.discussion.service.ModerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiscussionKafkaListener {
    private final ModerationService moderationService;
    private final KafkaTemplate<String, NoticeMessage> kafkaTemplate;

    @KafkaListener(topics = "InTopic", groupId = "discussion-group")
    public void listen(@Payload NoticeMessage message,
                       @Header(KafkaHeaders.CORRELATION_ID) String correlationId) {
        log.info("Received: newsId={}, correlationId={}, content={}",
                message.getNewsId(), correlationId, message.getContent());

        NoticeMessage response = moderationService.moderateAndSave(message);
        response.setCorrelationId(correlationId);

        kafkaTemplate.send(MessageBuilder
                .withPayload(response)
                .setHeader(KafkaHeaders.TOPIC, "OutTopic")
                .setHeader(KafkaHeaders.CORRELATION_ID, correlationId)
                .build());

        log.info("Sent response with correlationId={}, state={}", correlationId, response.getState());
    }
}