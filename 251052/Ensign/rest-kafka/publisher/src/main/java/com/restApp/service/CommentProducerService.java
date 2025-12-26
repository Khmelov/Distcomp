package com.restApp.service;

import com.restApp.config.KafkaConfig;
import com.restApp.dto.CommentRequestTo;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class CommentProducerService {

    private final KafkaTemplate<String, CommentRequestTo> kafkaTemplate;

    public CommentProducerService(KafkaTemplate<String, CommentRequestTo> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendComment(CommentRequestTo request) {
        kafkaTemplate.send(KafkaConfig.IN_TOPIC, String.valueOf(request.getNewsId()), request);
    }
}
