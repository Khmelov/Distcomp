
package com.example.task330.discussion.kafka;

import com.example.task330.discussion.dto.ReactionKafkaMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

// Отправка результатов обработки реакций в Kafka.
@Component
public class ReactionKafkaProducer {

    private static final String OUT_TOPIC = "OutTopic";

    private final KafkaTemplate<String, ReactionKafkaMessage> kafkaTemplate;

    // Spring передаёт KafkaTemplate через конструктор.
    public ReactionKafkaProducer(
            KafkaTemplate<String, ReactionKafkaMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // Отправка ответа в OutTopic.
    public void send(ReactionKafkaMessage message) {
        String key = message.getTopicId() == null
                ? "response"
                : message.getTopicId().toString();

        kafkaTemplate.send(OUT_TOPIC, key, message);
    }
}
