package by.rest.publisher.service;

import by.rest.publisher.dto.kafka.ModerationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class KafkaConsumerService {
    
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerService.class);
    
    private final CommentService commentService;
    
    @Value("${kafka.topic.out}")
    private String outTopic;
    
    public KafkaConsumerService(CommentService commentService) {
        this.commentService = commentService;
    }
    
    @KafkaListener(
        topics = "${kafka.topic.out}",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void listenModerationResult(
            @Payload ModerationResult result,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition,
            @Header(KafkaHeaders.OFFSET) Long offset,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        
        log.info("""
            Received moderation result from Kafka:
            Topic: {}
            Key: {}
            Partition: {}
            Offset: {}
            Comment ID: {}
            Status: {}
            Reason: {}
            Moderated At: {}
            """, 
            topic, key, partition, offset, 
            result.getCommentId(), result.getStatus(), 
            result.getReason(), result.getModeratedAt());
        
        try {
            // Обновляем статус комментария в БД
            UUID commentId = UUID.fromString(result.getCommentId());
            commentService.updateCommentStatus(commentId, result.getStatus());
            
            log.info("Successfully updated comment status: id={}, newStatus={}", 
                    commentId, result.getStatus());
            
        } catch (Exception e) {
            log.error("Failed to process moderation result for commentId={}: {}", 
                    result.getCommentId(), e.getMessage(), e);
            // Здесь можно добавить обработку ошибок
        }
    }
}