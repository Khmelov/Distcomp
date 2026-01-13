package com.distcomp.publisher.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PostProducer {
    
    private static final Logger logger = LoggerFactory.getLogger(PostProducer.class);
    private static final String TOPIC = "post-topic";
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    public void sendMessage(PostMessage message) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(TOPIC, jsonMessage);
            logger.info("Sent message to topic {}: {}", TOPIC, message.getOperation());
        } catch (JsonProcessingException e) {
            logger.error("Error serializing message: {}", e.getMessage());
        }
    }
}
