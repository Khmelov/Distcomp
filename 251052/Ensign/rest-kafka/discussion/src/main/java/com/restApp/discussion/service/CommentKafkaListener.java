package com.restApp.discussion.service;

import com.restApp.discussion.dto.CommentRequestTo;
import com.restApp.discussion.dto.CommentResponseTo;
import com.restApp.discussion.model.CommentState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class CommentKafkaListener {

    private static final Logger log = LoggerFactory.getLogger(CommentKafkaListener.class);
    private static final List<String> STOP_WORDS = Arrays.asList("bad", "spam", "illegal");
    private static final String OUT_TOPIC = "OutTopic";

    private final CommentService commentService;
    private final KafkaTemplate<String, CommentResponseTo> kafkaTemplate;

    public CommentKafkaListener(CommentService commentService, KafkaTemplate<String, CommentResponseTo> kafkaTemplate) {
        this.commentService = commentService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "InTopic", groupId = "discussion-group")
    public void consume(CommentRequestTo request) {
        log.info("Received comment request from InTopic for moderation: {}", request);

        // 1. Moderate (Comment already exists in PENDING state)
        CommentState state = moderate(request.content());

        // 2. Update state
        CommentResponseTo moderatedResponse = commentService.moderate(request.id(), request.country(), state);
        if (moderatedResponse != null) {
            log.info("Updated comment state to {}: {}", state, moderatedResponse);

            // 3. Send to OutTopic
            kafkaTemplate.send(OUT_TOPIC, String.valueOf(moderatedResponse.newsId()), moderatedResponse);
        } else {
            log.warn("Failed to find comment for moderation: id={}, country={}", request.id(), request.country());
        }
    }

    private CommentState moderate(String content) {
        if (content == null)
            return CommentState.APPROVE;
        String lowerContent = content.toLowerCase();
        for (String word : STOP_WORDS) {
            if (lowerContent.contains(word)) {
                return CommentState.DECLINE;
            }
        }
        return CommentState.APPROVE;
    }
}
