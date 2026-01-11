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
public class KafkaConsumerSimple {
    
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerSimple.class);
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    @Value("${kafka.topic.out}")
    private String outTopic;
    
    private final List<String> stopWords = Arrays.asList(
        "viagra", "casino", "gambling", "porn", "xxx", "spam"
    );
    
    public KafkaConsumerSimple(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    
    @KafkaListener(topics = "${kafka.topic.in}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(CommentKafkaRequest request) {
        log.info("🚀 RECEIVED from Kafka: {}", request.getCommentId());
        
        // Простая модерация
        String status = "APPROVE";
        String reason = "OK";
        
        for (String stopWord : stopWords) {
            if (request.getContent().toLowerCase().contains(stopWord)) {
                status = "DECLINE";
                reason = "Contains: " + stopWord;
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
        
        try {
            kafkaTemplate.send(outTopic, request.getStoryId().toString(), result);
            log.info("✅ Sent to OutTopic: {} -> {}", request.getCommentId(), status);
        } catch (Exception e) {
            log.error("❌ Failed to send to OutTopic: {}", e.getMessage());
        }
    }
}