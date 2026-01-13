package com.distcomp.discussion.kafka;

import com.distcomp.discussion.post.dto.PostRequest;
import com.distcomp.discussion.post.dto.PostResponse;
import com.distcomp.discussion.post.service.MockPostService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(PostConsumer.class);
    private static final String REQUEST_TOPIC = "post-topic";
    private static final String RESPONSE_TOPIC = "post-response-topic";
    
    @Autowired
    private MockPostService postService;
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @KafkaListener(topics = REQUEST_TOPIC, groupId = "discussion-group")
    public void handleMessage(String message) {
        try {
            PostMessage postMessage = objectMapper.readValue(message, PostMessage.class);
            logger.info("Received message with operation: {}", postMessage.getOperation());
            
            switch (postMessage.getOperation()) {
                case "CREATE":
                    handleCreate(postMessage);
                    break;
                case "GET_ALL":
                    handleGetAll(postMessage);
                    break;
                case "GET_BY_ID":
                    handleGetById(postMessage);
                    break;
                case "UPDATE":
                    handleUpdate(postMessage);
                    break;
                case "DELETE":
                    handleDelete(postMessage);
                    break;
                default:
                    logger.error("Unknown operation: {}", postMessage.getOperation());
            }
        } catch (JsonProcessingException e) {
            logger.error("Error deserializing message: {}", e.getMessage());
        }
    }
    
    private void handleCreate(PostMessage message) {
        try {
            PostRequest request = message.getPostRequest();
            PostResponse response = postService.create(request);
            sendResponse(PostResponseMessage.single(response, message.getRequestId()));
        } catch (Exception e) {
            logger.error("Error creating post: {}", e.getMessage());
            sendResponse(PostResponseMessage.error(e.getMessage(), message.getRequestId()));
        }
    }
    
    private void handleGetAll(PostMessage message) {
        try {
            List<PostResponse> responses = postService.listAll();
            sendResponse(PostResponseMessage.list(responses, message.getRequestId()));
        } catch (Exception e) {
            logger.error("Error getting all posts: {}", e.getMessage());
            sendResponse(PostResponseMessage.error(e.getMessage(), message.getRequestId()));
        }
    }
    
    private void handleGetById(PostMessage message) {
        try {
            PostRequest request = message.getPostRequest();
            Optional<PostResponse> response = postService.getById(request.getId());
            if (response.isPresent()) {
                sendResponse(PostResponseMessage.single(response.get(), message.getRequestId()));
            } else {
                sendResponse(PostResponseMessage.error("Post not found", message.getRequestId()));
            }
        } catch (Exception e) {
            logger.error("Error getting post by ID: {}", e.getMessage());
            sendResponse(PostResponseMessage.error(e.getMessage(), message.getRequestId()));
        }
    }
    
    private void handleUpdate(PostMessage message) {
        try {
            PostRequest request = message.getPostRequest();
            Optional<PostResponse> response = postService.updateById(request.getId(), request);
            if (response.isPresent()) {
                sendResponse(PostResponseMessage.single(response.get(), message.getRequestId()));
            } else {
                sendResponse(PostResponseMessage.error("Post not found", message.getRequestId()));
            }
        } catch (Exception e) {
            logger.error("Error updating post: {}", e.getMessage());
            sendResponse(PostResponseMessage.error(e.getMessage(), message.getRequestId()));
        }
    }
    
    private void handleDelete(PostMessage message) {
        try {
            PostRequest request = message.getPostRequest();
            boolean deleted = postService.deleteById(request.getId());
            sendResponse(PostResponseMessage.deleted(deleted, message.getRequestId()));
        } catch (Exception e) {
            logger.error("Error deleting post: {}", e.getMessage());
            sendResponse(PostResponseMessage.error(e.getMessage(), message.getRequestId()));
        }
    }
    
    private void sendResponse(PostResponseMessage response) {
        try {
            String jsonResponse = objectMapper.writeValueAsString(response);
            kafkaTemplate.send(RESPONSE_TOPIC, jsonResponse);
            logger.info("Sent response for request ID: {}", response.getRequestId());
        } catch (JsonProcessingException e) {
            logger.error("Error serializing response: {}", e.getMessage());
        }
    }
}
