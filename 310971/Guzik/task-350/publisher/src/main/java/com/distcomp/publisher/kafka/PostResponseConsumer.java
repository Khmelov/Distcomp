package com.distcomp.publisher.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class PostResponseConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(PostResponseConsumer.class);
    private static final String RESPONSE_TOPIC = "post-response-topic";
    
    private final ConcurrentMap<Long, PostResponseMessage> responseCache = new ConcurrentHashMap<>();
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @KafkaListener(topics = RESPONSE_TOPIC, groupId = "publisher-group")
    public void receiveResponse(String message) {
        try {
            PostResponseMessage response = objectMapper.readValue(message, PostResponseMessage.class);
            responseCache.put(response.getRequestId(), response);
            logger.info("Received response for request ID: {}", response.getRequestId());
        } catch (JsonProcessingException e) {
            logger.error("Error deserializing response message: {}", e.getMessage());
        }
    }
    
    public PostResponseMessage getResponse(Long requestId) {
        return responseCache.get(requestId);
    }
    
    public void removeResponse(Long requestId) {
        responseCache.remove(requestId);
    }
}
