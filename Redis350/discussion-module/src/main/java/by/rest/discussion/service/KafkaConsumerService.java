package by.rest.discussion.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class KafkaConsumerService {
    
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    
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
    
    @KafkaListener(
        topics = "${kafka.topic.in}",
        groupId = "${spring.kafka.consumer.group-id:discussion-group}"
    )
    public void listen(Map<String, Object> message) {
        try {
            // Преобразуем Map в нужный формат
            String commentId = (String) message.get("commentId");
            String content = (String) message.get("content");
            Number storyIdNumber = (Number) message.get("storyId");
            Long storyId = storyIdNumber != null ? storyIdNumber.longValue() : null;
            
            log.info("""
                ========================================
                📥 RECEIVED FROM KAFKA
                Comment ID: {}
                Story ID:   {}
                Content:    {}
                ========================================
                """, 
                commentId, storyId, 
                content != null ? content.substring(0, Math.min(50, content.length())) : "");
            
            // Модерация
            String status = "APPROVE";
            String reason = "Comment passed moderation";
            
            if (content != null) {
                String contentLower = content.toLowerCase();
                
                for (String stopWord : stopWords) {
                    if (contentLower.contains(stopWord)) {
                        status = "DECLINE";
                        reason = "Contains forbidden word: " + stopWord;
                        break;
                    }
                }
                
                if (content.trim().length() < 5) {
                    status = "DECLINE";
                    reason = "Too short (minimum 5 characters required)";
                }
            }
            
            // Создаем результат
            Map<String, Object> result = Map.of(
                "commentId", commentId,
                "storyId", storyId,
                "status", status,
                "reason", reason,
                "moderatedAt", java.time.LocalDateTime.now().toString(),
                "moderator", "auto-moderation"
            );
            
            // Отправляем результат
            String key = String.valueOf(storyId);
            kafkaTemplate.send(outTopic, key, result);
            
            log.info("""
                ========================================
                ✅ MODERATION RESULT SENT TO OUTTOPIC
                Comment ID: {}
                Status:     {}
                Reason:     {}
                ========================================
                """, 
                commentId, status, reason);
            
        } catch (Exception e) {
            log.error("❌ Error processing Kafka message: {}", e.getMessage(), e);
        }
    }
}