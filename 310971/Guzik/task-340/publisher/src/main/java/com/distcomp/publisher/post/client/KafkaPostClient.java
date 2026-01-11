package com.distcomp.publisher.post.client;

import com.distcomp.publisher.kafka.PostMessage;
import com.distcomp.publisher.kafka.PostProducer;
import com.distcomp.publisher.kafka.PostResponseConsumer;
import com.distcomp.publisher.kafka.PostResponseMessage;
import com.distcomp.publisher.post.dto.PostRequest;
import com.distcomp.publisher.post.dto.PostResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class KafkaPostClient {
    
    private static final Logger logger = LoggerFactory.getLogger(KafkaPostClient.class);
    
    @Autowired
    private PostProducer postProducer;
    
    @Autowired
    private PostResponseConsumer responseConsumer;
    
    private final AtomicLong requestIdGenerator = new AtomicLong(1);
    
    public PostResponse create(PostRequest request) {
        Long requestId = requestIdGenerator.getAndIncrement();
        PostMessage message = new PostMessage("CREATE", request, requestId);
        postProducer.sendMessage(message);
        
        return waitForResponse(requestId);
    }
    
    public List<PostResponse> listAll() {
        Long requestId = requestIdGenerator.getAndIncrement();
        PostMessage message = new PostMessage("GET_ALL", null, requestId);
        postProducer.sendMessage(message);
        
        PostResponseMessage response = waitForResponseMessage(requestId);
        return response != null ? response.getPostResponses() : null;
    }
    
    public PostResponse getById(Long id) {
        Long requestId = requestIdGenerator.getAndIncrement();
        PostRequest request = new PostRequest();
        request.setId(id);
        PostMessage message = new PostMessage("GET_BY_ID", request, requestId);
        postProducer.sendMessage(message);
        
        return waitForResponse(requestId);
    }
    
    public PostResponse updateById(Long id, PostRequest request) {
        request.setId(id);
        Long requestId = requestIdGenerator.getAndIncrement();
        PostMessage message = new PostMessage("UPDATE", request, requestId);
        postProducer.sendMessage(message);
        
        return waitForResponse(requestId);
    }
    
    public boolean deleteById(Long id) {
        Long requestId = requestIdGenerator.getAndIncrement();
        PostRequest request = new PostRequest();
        request.setId(id);
        PostMessage message = new PostMessage("DELETE", request, requestId);
        postProducer.sendMessage(message);
        
        PostResponseMessage response = waitForResponseMessage(requestId);
        return response != null && response.isDeleted();
    }
    
    private PostResponse waitForResponse(Long requestId) {
        PostResponseMessage response = waitForResponseMessage(requestId);
        return response != null ? response.getPostResponse() : null;
    }
    
    private PostResponseMessage waitForResponseMessage(Long requestId) {
        int maxAttempts = 10;
        int attempt = 0;
        
        while (attempt < maxAttempts) {
            PostResponseMessage response = responseConsumer.getResponse(requestId);
            if (response != null) {
                responseConsumer.removeResponse(requestId);
                return response;
            }
            
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Interrupted while waiting for response", e);
                return null;
            }
            attempt++;
        }
        
        logger.error("Timeout waiting for response with request ID: {}", requestId);
        return null;
    }
}
