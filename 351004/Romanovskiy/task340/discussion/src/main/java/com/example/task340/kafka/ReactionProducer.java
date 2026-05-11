package com.example.task340.kafka;

import com.example.task340.domain.dto.kafka.ReactionMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReactionProducer {

    private final KafkaTemplate<String, ReactionMessage> kafkaTemplate;
    private static final String TOPIC_OUT = "out-topic";

    public void sendToOutTopic(ReactionMessage message) {
        // Используем ID твита как ключ для партиционирования
        String key = String.valueOf(message.getTweetId());
        
        kafkaTemplate.send(TOPIC_OUT, key, message)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Sent message={} with offset={}", message, result.getRecordMetadata().offset());
                    } else {
                        log.error("Unable to send message={} due to : {}", message, ex.getMessage(), ex);
                    }
                });
    }
}
