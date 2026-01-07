package com.task310.blogplatform.service.kafka;

import tools.jackson.databind.ObjectMapper;
import com.task310.blogplatform.dto.PostResponseTo;
import com.task310.blogplatform.dto.kafka.PostKafkaResponse;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PostKafkaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(PostKafkaConsumer.class);
    
    @Value("${spring.kafka.consumer.topic.out:OutTopic}")
    private String outTopic;
    
    private final ObjectMapper objectMapper;
    private final Map<Long, PostResponseTo> pendingResponses = new ConcurrentHashMap<>();
    private final Map<Long, PostResponseTo> responsesByArticle = new ConcurrentHashMap<>();

    @Autowired
    public PostKafkaConsumer(@Qualifier("kafkaObjectMapper") ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${spring.kafka.consumer.topic.out:OutTopic}", groupId = "${spring.kafka.consumer.group-id:publisher-group}", containerFactory = "kafkaListenerContainerFactory")
    public void consume(@Payload Object messagePayload,
                       @Header(KafkaHeaders.RECEIVED_KEY) String key,
                       Acknowledgment acknowledgment) {
        logger.info("Received raw message from OutTopic: payload type={}, key={}, payload={}", 
            messagePayload != null ? messagePayload.getClass().getName() : "null", key, messagePayload);
        try {
            // Extract value from ConsumerRecord if needed
            Object actualPayload = messagePayload;
            if (messagePayload instanceof ConsumerRecord) {
                ConsumerRecord<?, ?> record = (ConsumerRecord<?, ?>) messagePayload;
                actualPayload = record.value();
                logger.info("Extracted value from ConsumerRecord: {}", actualPayload);
            }
            
            // Deserialize Object (Map) to PostKafkaResponse
            PostKafkaResponse response;
            if (actualPayload instanceof PostKafkaResponse) {
                response = (PostKafkaResponse) actualPayload;
            } else {
                // Convert Map or other Object to PostKafkaResponse
                response = objectMapper.readValue(
                    objectMapper.writeValueAsString(actualPayload), 
                    PostKafkaResponse.class
                );
            }
            logger.info("Deserialized message from OutTopic: id={}, articleId={}, state={}", 
                response.getId(), response.getArticleId(), response.getState());
            
            // Convert Kafka response to PostResponseTo
            PostResponseTo postResponse = convertToPostResponseTo(response);
            
            // Store response by post ID
            pendingResponses.put(response.getId(), postResponse);
            
            // Store response by article ID (for latest response lookup)
            // Always update to latest response for the article
            responsesByArticle.put(response.getArticleId(), postResponse);
            logger.info("Stored response for articleId={}, postId={}, created={}, state={}", 
                response.getArticleId(), response.getId(), response.getCreated(), response.getState());
            
            acknowledgment.acknowledge();
        } catch (Exception e) {
            logger.error("Error processing message from OutTopic: {}", e.getMessage(), e);
            // Acknowledge even on error to avoid infinite retry loop
            acknowledgment.acknowledge();
        }
    }

    public PostResponseTo getResponse(Long postId) {
        return pendingResponses.remove(postId);
    }

    public boolean hasResponse(Long postId) {
        return pendingResponses.containsKey(postId);
    }

    public PostResponseTo getLatestResponseForArticle(Long articleId, long afterTimestamp) {
        PostResponseTo response = responsesByArticle.get(articleId);
        if (response != null && response.getCreated() != null) {
            // Check if response was created after the timestamp
            long createdMillis = response.getCreated().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
            if (createdMillis > afterTimestamp) {
                // Remove and return the response
                PostResponseTo result = responsesByArticle.remove(articleId);
                logger.info("Found response for articleId={} created at {} (after {})", 
                    articleId, createdMillis, afterTimestamp);
                return result;
            } else {
                logger.debug("Response for articleId={} created at {} is not after {}", 
                    articleId, createdMillis, afterTimestamp);
            }
        } else {
            logger.debug("No response found for articleId={} or response has no created timestamp", articleId);
        }
        return null;
    }

    private PostResponseTo convertToPostResponseTo(PostKafkaResponse kafkaResponse) {
        PostResponseTo response = new PostResponseTo();
        response.setId(kafkaResponse.getId());
        response.setArticleId(kafkaResponse.getArticleId());
        response.setContent(kafkaResponse.getContent());
        response.setCreated(kafkaResponse.getCreated());
        response.setModified(kafkaResponse.getModified());
        response.setState(kafkaResponse.getState());
        return response;
    }
}

