package com.example.demo.consumer;

import com.example.demo.dto.responses.MessageKafkaResponseTo;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MessageResultConsumer {
    @KafkaListener(topics = "OutTopic", groupId = "publisher-group")
    public void consume(MessageKafkaResponseTo response) {
        System.out.println("Received response: " + response);
    }
}
