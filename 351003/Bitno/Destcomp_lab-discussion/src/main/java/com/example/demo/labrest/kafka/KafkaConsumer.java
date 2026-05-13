package com.example.demo.labrest.kafka;

import com.example.demo.labrest.dto.KafkaNoticeRequest;
import com.example.demo.labrest.service.DiscussionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {

    private final DiscussionService service;

    @KafkaListener(
            topics = "InTopic",
            groupId = "${spring.kafka.consumer.group-id:discussion-group}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeNotice(KafkaNoticeRequest request) {
        log.info("📥 Kafka: Received notice request - Topic: {}, ID: {}, Content: {}",
                request.getTopicId(),
                request.getId(),
                truncate(request.getContent(), 50));

        try {
            var result = service.processNotice(request);
            log.info("✅ Kafka: Notice processed successfully - State: {}", result.getState());
        } catch (Exception e) {
            log.error("❌ Kafka: Error processing notice: {}", request, e);
        }
    }

    private String truncate(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + "...";
    }
}