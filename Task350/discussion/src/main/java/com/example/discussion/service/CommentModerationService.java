package com.example.discussion.service;

import com.example.discussion.dto.CommentRequestTo;
import com.example.discussion.dto.CommentResponseTo;
import com.example.discussion.dto.CommentState;
import com.example.discussion.model.Comment;
import com.example.discussion.repository.CommentRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CommentModerationService {
    private final CommentRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final AtomicLong idGenerator = new AtomicLong(1);

    // Стоп-слова — можно расширить
    private static final Set<String> STOP_WORDS = Set.of("bad", "spam", "viagra", "xxx");

    public CommentModerationService(CommentRepository repository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "InTopic", groupId = "discussion-group")
    public void consume(CommentRequestTo request) {
        CommentState state = moderate(request.content());
        Long id = idGenerator.getAndIncrement();
        Comment comment = new Comment(request.storyId(), id, request.content());
        comment.setState(state);
        comment.setCreated(Instant.now());
        repository.save(comment);

        // Отправляем обратно в publisher
        CommentResponseTo response = new CommentResponseTo(
                id, request.storyId(), request.content(), comment.getCreated(), state
        );
        kafkaTemplate.send("OutTopic", request.storyId().toString(), response);
    }

    private CommentState moderate(String content) {
        String lower = content.toLowerCase();
        for (String stop : STOP_WORDS) {
            if (lower.contains(stop)) {
                return CommentState.DECLINE;
            }
        }
        return CommentState.APPROVE;
    }
}