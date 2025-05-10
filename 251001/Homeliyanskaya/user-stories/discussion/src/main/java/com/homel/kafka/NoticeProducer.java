package com.homel.kafka;

import com.homel.dto.NoticeResponseTo;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class NoticeProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public NoticeProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendToOutTopic(Object response) {
        kafkaTemplate.send("OutTopic", response);
    }
}