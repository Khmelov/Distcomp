package com.example.lab.publisher.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import com.example.lab.publisher.dto.MarkerResponseTo;
import com.example.lab.publisher.dto.NewsResponseTo;
import com.example.lab.publisher.dto.UserResponseTo;
import com.example.lab.publisher.dto.kafka.KafkaMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducerService {
    
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    @Value("${app.kafka.topics.user:user-topic}")
    private String userTopic;
    
    @Value("${app.kafka.topics.news:news-topic}")
    private String newsTopic;
    
    @Value("${app.kafka.topics.marker:marker-topic}")
    private String markerTopic;
    
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    
    // ==================== USER EVENTS ====================
    
    public void sendUserCreated(UserResponseTo user) {
        sendMessage(userTopic, "CREATE", "User", user);
    }
    
    public void sendUserUpdated(UserResponseTo user) {
        sendMessage(userTopic, "UPDATE", "User", user);
    }
    
    public void sendUserDeleted(Long userId) {
        UserResponseTo deletedUser = new UserResponseTo();
        deletedUser.setId(userId);
        sendMessage(userTopic, "DELETE", "User", deletedUser);
    }
    
    // ==================== NEWS EVENTS ====================
    
    public void sendNewsCreated(NewsResponseTo news) {
        sendMessage(newsTopic, "CREATE", "News", news);
    }
    
    public void sendNewsUpdated(NewsResponseTo news) {
        sendMessage(newsTopic, "UPDATE", "News", news);
    }
    
    public void sendNewsDeleted(Long newsId) {
        NewsResponseTo deletedNews = new NewsResponseTo();
        deletedNews.setId(newsId);
        sendMessage(newsTopic, "DELETE", "News", deletedNews);
    }
    
    // ==================== MARKER EVENTS ====================
    
    public void sendMarkerCreated(MarkerResponseTo marker) {
        sendMessage(markerTopic, "CREATE", "Marker", marker);
    }
    
    public void sendMarkerUpdated(MarkerResponseTo marker) {
        sendMessage(markerTopic, "UPDATE", "Marker", marker);
    }
    
    public void sendMarkerDeleted(Long markerId) {
        MarkerResponseTo deletedMarker = new MarkerResponseTo();
        deletedMarker.setId(markerId);
        sendMessage(markerTopic, "DELETE", "Marker", deletedMarker);
    }
    
    // ==================== PRIVATE METHODS ====================
    
    private <T> void sendMessage(String topic, String eventType, String entityType, T data) {
        try {
            KafkaMessage<T> message = new KafkaMessage<>(eventType, entityType, data);
            String jsonMessage = objectMapper.writeValueAsString(message);
            String key = entityType + "_" + getEntityId(data);
            
            CompletableFuture<SendResult<String, String>> future = 
                    kafkaTemplate.send(topic, key, jsonMessage);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.info("Sent message to topic {}: event={}, entity={}, id={}, offset={}", 
                            topic, eventType, entityType, getEntityId(data), 
                            result.getRecordMetadata().offset());
                } else {
                    logger.error("Failed to send message to topic {}: event={}, entity={}, id={}", 
                            topic, eventType, entityType, getEntityId(data), ex);
                }
            });
            
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize message for topic: {}", topic, e);
        }
    }
    
    private <T> Object getEntityId(T data) {
        if (data instanceof UserResponseTo) {
            return ((UserResponseTo) data).getId();
        } else if (data instanceof NewsResponseTo) {
            return ((NewsResponseTo) data).getId();
        } else if (data instanceof MarkerResponseTo) {
            return ((MarkerResponseTo) data).getId();
        }
        return "unknown";
    }
}