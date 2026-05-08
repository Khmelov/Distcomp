package com.example.demo.producer;

import com.example.demo.dto.responses.MessageKafkaResponseTo;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageResultProducer {
    private final KafkaTemplate<String, MessageKafkaResponseTo> kafkaTemplate;
    private final String topic = "OutTopic";

    public MessageResultProducer(KafkaTemplate<String, MessageKafkaResponseTo> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(MessageKafkaResponseTo response) {
        kafkaTemplate.send(topic, String.valueOf(response.getIssueId()), response);
    }
}
