package com.example.restservice.kafka;

import com.example.restservice.model.MessageEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaMessageProducer {
    private final KafkaTemplate<String, MessageEvent> kafkaTemplate;

    public void sendMessage(MessageEvent message) {
        kafkaTemplate.send("InTopic", message.getArticleId().toString(), message);
    }
}
