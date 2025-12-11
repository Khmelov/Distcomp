package org.example.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.kafka.dto.KafkaCommentMessage;
import org.example.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DiscussionKafkaListener {

    private static final Logger log = LoggerFactory.getLogger(DiscussionKafkaListener.class);

    private final CommentService commentService;
    private final DiscussionKafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;

    public DiscussionKafkaListener(CommentService commentService, DiscussionKafkaProducer kafkaProducer, ObjectMapper objectMapper) {
        this.commentService = commentService;
        this.kafkaProducer = kafkaProducer;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "InTopic", groupId = "discussion-group")
    public void listen(Map<String, Object> message1) {
        var message = objectMapper.convertValue(message1, KafkaCommentMessage.class);
        log.info("Received from InTopic: {}", message);

        if (message.getState() == null || message.getState().isEmpty()) {
            message.setState("PENDING");
        }

        if ("DELETE".equals(message.getOperation())) {
            commentService.deleteFromKafka(message.getId());
            return;
        }

        String finalState = moderate(message.getContent());
        message.setState(finalState);

        if ("CREATE".equals(message.getOperation())) {
            commentService.createFromKafka(message);
        } else if ("UPDATE".equals(message.getOperation())) {
            commentService.updateFromKafka(message);
        }

        kafkaProducer.sendToOutTopic(message);
    }

    private String moderate(String content) {
        String[] stopWords = {"блядь", "хуй", "пизда", "нахуй", "говно"};
        String lowerContent = content.toLowerCase();
        for (String word : stopWords) {
            if (lowerContent.contains(word)) {
                return "DECLINE";
            }
        }
        return "APPROVE";
    }
}