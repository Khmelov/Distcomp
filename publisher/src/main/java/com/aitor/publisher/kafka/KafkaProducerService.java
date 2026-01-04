package com.aitor.publisher.kafka;

import com.aitor.publisher.dto.MessageRequestTo;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, MessageRequestTo> kafkaTemplate;

    public void sendMessage(MessageRequestTo messageRequestTo, String key) {
        kafkaTemplate.send("InTopic", key, messageRequestTo);
    }

    public void sendMessage(String key) {
        kafkaTemplate.send("InTopic", key, null);
    }
}
