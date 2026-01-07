package com.task310.blogplatform.service.kafka;

import com.task310.blogplatform.dto.kafka.PostKafkaRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class PostKafkaProducer {

    private static final Logger logger = LoggerFactory.getLogger(PostKafkaProducer.class);
    
    @Value("${spring.kafka.producer.topic.in:InTopic}")
    private String inTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public PostKafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendPostRequest(PostKafkaRequest request) {
        // Use articleId as key to ensure messages for same article go to same partition
        String key = String.valueOf(request.getArticleId());
        
        try {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(inTopic, key, request);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.info("Sent message=[{}] with offset=[{}] to partition=[{}]",
                        request, result.getRecordMetadata().offset(), result.getRecordMetadata().partition());
                } else {
                    logger.error("Unable to send message=[{}] due to : {}", request, ex.getMessage(), ex);
                }
            });
            
            // Wait for the send to complete (with timeout) to catch immediate errors
            try {
                future.get(5, java.util.concurrent.TimeUnit.SECONDS);
            } catch (java.util.concurrent.TimeoutException e) {
                logger.warn("Kafka send timed out, but message may still be sent");
            } catch (java.util.concurrent.ExecutionException e) {
                logger.error("Failed to send message to Kafka", e);
                throw new RuntimeException("Failed to send message to Kafka: " + e.getCause().getMessage(), e.getCause());
            }
        } catch (Exception e) {
            logger.error("Failed to send message to Kafka", e);
            throw new RuntimeException("Failed to construct kafka producer: " + e.getMessage(), e);
        }
    }
}

