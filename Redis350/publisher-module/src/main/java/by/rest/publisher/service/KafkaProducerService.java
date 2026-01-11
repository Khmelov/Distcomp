package by.rest.publisher.service;

import by.rest.publisher.dto.kafka.CommentKafkaRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class KafkaProducerService {
    
    private static final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    @Value("${kafka.topic.in}")
    private String inTopic;
    
    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    
    public void sendCommentForModeration(CommentKafkaRequest request) {
        try {
            // Ключ = storyId для правильного partitioning
            String key = String.valueOf(request.getStoryId());
            
            log.info("Sending comment to Kafka topic '{}': key={}, commentId={}, storyId={}", 
                     inTopic, key, request.getCommentId(), request.getStoryId());
            
            // Создаем сообщение с заголовком
            Message<CommentKafkaRequest> message = MessageBuilder
                .withPayload(request)
                .setHeader(KafkaHeaders.TOPIC, inTopic)
                .setHeader(KafkaHeaders.KEY, key)
                .setHeader("X-Correlation-Id", UUID.randomUUID().toString())
                .build();
            
            // Отправляем асинхронно
            kafkaTemplate.send(message)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Successfully sent comment to Kafka: topic={}, partition={}, offset={}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to send comment to Kafka: {}", ex.getMessage(), ex);
                        // Здесь можно добавить retry логику или сохранение в dead letter queue
                    }
                });
            
        } catch (Exception e) {
            log.error("Error sending comment to Kafka: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send comment to Kafka", e);
        }
    }
}