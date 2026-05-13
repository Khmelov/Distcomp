package com.example.demo.labrest.kafka;

import com.example.demo.labrest.dto.KafkaNoticeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {

    private final KafkaTemplate<String, KafkaNoticeResponse> kafkaTemplate;

    public void sendModerationResult(KafkaNoticeResponse response) {
        log.info("📤 Kafka: Sending moderation result - Topic: {}, ID: {}, State: {}",
                response.getTopicId(),
                response.getId(),
                response.getState());

        String key = String.valueOf(response.getTopicId());

        CompletableFuture<SendResult<String, KafkaNoticeResponse>> future =
                kafkaTemplate.send("OutTopic", key, response);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("✅ Kafka: Message sent successfully - Offset: {}",
                        result.getRecordMetadata().offset());
            } else {
                log.error("❌ Kafka: Failed to send message", ex);
            }
        });
    }

    public void sendError(KafkaNoticeResponse errorResponse) {
        log.warn("⚠️ Kafka: Sending error response - {}", errorResponse);
        sendModerationResult(errorResponse);
    }
}