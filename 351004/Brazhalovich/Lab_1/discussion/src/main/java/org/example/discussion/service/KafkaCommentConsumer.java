package org.example.discussion.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.discussion.dto.kafka.CommentKafkaMessage;
import org.example.discussion.entity.Comment;
import org.example.discussion.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaCommentConsumer {

    private final CommentRepository commentRepository;
    private final KafkaTemplate<String, CommentKafkaMessage> kafkaTemplate;

    @Value("${app.kafka.out-topic}")
    private String outTopic;

    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList("spam", "bad", "offensive"));

    @KafkaListener(topics = "${app.kafka.in-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(CommentKafkaMessage message) {
        log.info("Received message from InTopic: {}", message);

        String state = moderate(message.getContent()) ? "APPROVE" : "DECLINE";

        if ("APPROVE".equals(state)) {
            Comment comment = Comment.builder()
                    .id(message.getId())
                    .newsId(message.getNewsId())
                    .content(message.getContent())
                    .build();
            commentRepository.save(comment);
        }

        CommentKafkaMessage response = new CommentKafkaMessage(
                message.getId(),
                message.getNewsId(),
                message.getContent(),
                state
        );
        kafkaTemplate.send(outTopic, String.valueOf(message.getNewsId()), response);
        log.info("Sent response to OutTopic: {}", response);
    }

    private boolean moderate(String content) {
        for (String word : STOP_WORDS) {
            if (content.toLowerCase().contains(word.toLowerCase())) {
                return false;
            }
        }
        return true;
    }
}