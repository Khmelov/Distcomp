package com.example.demo.producer;

import com.example.demo.dto.requests.MessageKafkaRequestTo;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageKafkaProducer {
    private final KafkaTemplate<String, MessageKafkaRequestTo> kafkaTemplate;
    private final String topic = "InTopic";

    public MessageKafkaProducer(KafkaTemplate<String, MessageKafkaRequestTo> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(MessageKafkaRequestTo request) {
        kafkaTemplate.send(topic, String.valueOf(request.getIssueId()), request);
    }
}
