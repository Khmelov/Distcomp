package org.example.discussion.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.discussion.model.Comment;
import org.example.discussion.model.CommentState;
import org.example.discussion.repository.CommentRepository;
import org.example.discussion.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class KafkaConsumerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final CommentService commentService;
    private final KafkaProducerService kafkaProducerService;
    private final ObjectMapper objectMapper;
    private final CommentRepository commentRepository;

    public KafkaConsumerService(CommentService commentService,
                                KafkaProducerService kafkaProducerService,
                                ObjectMapper objectMapper,
                                CommentRepository commentRepository) {
        this.commentService = commentService;
        this.kafkaProducerService = kafkaProducerService;
        this.objectMapper = objectMapper;
        this.commentRepository = commentRepository;
    }

    @KafkaListener(topics = "InTopic", groupId = "discussion-group")
    public void listenInTopic(String rawMessage) {
        try {
            CommentKafkaMessage message = objectMapper.readValue(rawMessage, CommentKafkaMessage.class);
            log.info("Received from InTopic: id={}, op={}", message.getId(), message.getOperation());

            CommentKafkaMessage response = new CommentKafkaMessage();
            response.setId(message.getId());
            response.setArticleId(message.getArticleId());
            response.setContent(message.getContent());
            response.setOperation(message.getOperation());

            switch (message.getOperation()) {
                case CREATE, UPDATE -> {
                    String newState = commentService.moderate(message.getContent());
                    Comment existing = commentRepository.findAll().stream()
                            .filter(c -> c.getId().equals(message.getId()))
                            .findFirst().orElse(null);
                    if (existing != null) {
                        existing.setState(newState);
                        existing.setModified(LocalDateTime.now());
                        commentRepository.save(existing);
                    }
                    response.setState(newState);
                }
                case DELETE -> {
                    response.setState("DELETED");
                }
            }

            kafkaProducerService.sendToOutTopic(response);
        } catch (Exception e) {
            log.error("Error processing Kafka message: {}", e.getMessage(), e);
        }
    }
}