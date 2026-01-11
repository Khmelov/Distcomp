package by.rest.discussion.service;

import by.rest.discussion.dto.kafka.CommentKafkaRequest;
import by.rest.discussion.dto.kafka.ModerationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class KafkaConsumerService {
    
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerService.class);
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    @Value("${kafka.topic.out}")
    private String outTopic;
    
    private final List<String> stopWords = Arrays.asList(
        "viagra", "casino", "gambling", "porn", "xxx", "spam",
        "реклама", "казино", "азарт", "порно", "спам", "скам"
    );
    
    public KafkaConsumerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    
    @KafkaListener(topics = "${kafka.topic.in}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(CommentKafkaRequest request) {
        log.info("""
            ========================================
            📥 RECEIVED FROM KAFKA
            Comment ID: {}
            Story ID:   {}
            Content:    {}
            ========================================
            """, 
            request.getCommentId(), request.getStoryId(), 
            request.getContent().substring(0, Math.min(50, request.getContent().length())));
        
        try {
            // Простая модерация
            String content = request.getContent().toLowerCase();
            String status = "APPROVE";
            String reason = "Comment passed moderation";
            
            for (String stopWord : stopWords) {
                if (content.contains(stopWord.toLowerCase())) {
                    status = "DECLINE";
                    reason = "Contains forbidden word: " + stopWord;
                    break;
                }
            }
            
            // Отправляем результат
            ModerationResult result = new ModerationResult(
                request.getCommentId(),
                request.getStoryId(),
                status,
                reason
            );
            
            String key = String.valueOf(request.getStoryId());
            kafkaTemplate.send(outTopic, key, result);
            
            log.info("""
                ========================================
                ✅ MODERATION RESULT SENT TO OUTTOPIC
                Comment ID: {}
                Status:     {}
                Reason:     {}
                ========================================
                """, 
                request.getCommentId(), status, reason);
            
        } catch (Exception e) {
            log.error("❌ Error processing comment {}: {}", 
                request.getCommentId(), e.getMessage());
        }
    }
}