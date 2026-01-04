package com.example.publisher.service;

import com.example.publisher.dto.CommentRequestTo;
import com.example.publisher.dto.CommentResponseTo;
import com.example.publisher.model.Comment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class CommentKafkaService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public CommentKafkaService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendToInTopic(CommentRequestTo request) {
        kafkaTemplate.send("InTopic", request.storyId().toString(), request);
    }

    // Для обратной совместимости — оставляем REST-отправку
    // В production можно убрать
}