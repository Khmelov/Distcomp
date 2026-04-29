package com.example.task361.kafka;

import com.example.task361.domain.dto.kafka.ReactionMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class ReactionResponseManager {
    
    private final Map<Long, ReactionMessage> responses = new ConcurrentHashMap<>();
    private final Map<Long, CountDownLatch> latches = new ConcurrentHashMap<>();
    
    public void registerPending(Long reactionId) {
        latches.put(reactionId, new CountDownLatch(1));
        log.debug("Registered pending reaction: {}", reactionId);
    }
    
    public void storeResponse(ReactionMessage response) {
        responses.put(response.getId(), response);
        CountDownLatch latch = latches.get(response.getId());
        if (latch != null) {
            latch.countDown();
            log.debug("Stored response for reaction: {}", response.getId());
        }
    }
    
    public ReactionMessage waitForResponse(Long reactionId, long timeoutSeconds) 
            throws InterruptedException {
        CountDownLatch latch = latches.get(reactionId);
        if (latch == null) {
            return null;
        }
        
        if (latch.await(timeoutSeconds, TimeUnit.SECONDS)) {
            return responses.remove(reactionId);
        } else {
            log.warn("Timeout waiting for response for reaction: {}", reactionId);
            return null;
        }
    }
    
    public void cleanup(Long reactionId) {
        latches.remove(reactionId);
        responses.remove(reactionId);
    }
}
