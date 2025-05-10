package com.example.discussion.kafka;

import com.example.discussion.dto.MessageResponseKafka;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, MessageResponseKafka> kafkaTemplate;

    public void sendResponse(MessageResponseKafka response) {
        kafkaTemplate.send("OutTopic", response);
    }
}