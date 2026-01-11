package by.rest.discussion.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class KafkaProducerService {
    
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    @Value("${kafka.topic.out:OutTopic}")
    private String commentOutTopic;
    
    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    
    public void sendTestMessage() {
        try {
            String message = "Test message from discussion module at " + java.time.Instant.now();
            kafkaTemplate.send("TestTopic", "test-key", message);
            logger.info("Test message sent successfully");
        } catch (Exception e) {
            logger.error("Failed to send test message: {}", e.getMessage());
        }
    }
}