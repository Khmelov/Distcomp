package by.rest.discussion.service;

import by.rest.discussion.dto.kafka.CommentKafkaRequest;
import by.rest.discussion.dto.kafka.ModerationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class QuickKafkaConsumer {
    
    private static final Logger log = LoggerFactory.getLogger(QuickKafkaConsumer.class);
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final QuickModerationService moderationService;
    
    @Value("${kafka.topic.out:OutTopic}")
    private String outTopic;
    
    public QuickKafkaConsumer(KafkaTemplate<String, Object> kafkaTemplate,
                             QuickModerationService moderationService) {
        this.kafkaTemplate = kafkaTemplate;
        this.moderationService = moderationService;
    }
    
    @KafkaListener(topics = "${kafka.topic.in:InTopic}", groupId = "${spring.kafka.consumer.group-id:discussion-group}")
    public void consumeComment(CommentKafkaRequest request) {
        log.info("📥 Received comment for moderation: {}", request.getCommentId());
        
        try {
            // Модерируем
            ModerationResult result = moderationService.moderate(request);
            
            // Отправляем результат
            String key = String.valueOf(request.getStoryId());
            kafkaTemplate.send(outTopic, key, result);
            
            log.info("📤 Sent to OutTopic: {} -> {}", request.getCommentId(), result.getStatus());
            
        } catch (Exception e) {
            log.error("❌ Error processing comment {}: {}", request.getCommentId(), e.getMessage());
        }
    }
}