package com.example.restservice.kafka;

import com.example.restservice.model.MessageEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaResponseProducer {
    private final KafkaTemplate<String, MessageEvent> kafkaTemplate;

    public void sendResponse(MessageEvent message) {
        kafkaTemplate.send("OutTopic", message.getArticleId().toString(), message);
    }
}
