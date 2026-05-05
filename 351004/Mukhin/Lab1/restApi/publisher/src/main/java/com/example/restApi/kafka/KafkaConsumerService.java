package com.example.restApi.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
public class KafkaConsumerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final ObjectMapper objectMapper;
    private final Map<Long, CommentKafkaMessage> responses = new ConcurrentHashMap<>();
    private final Map<Long, CountDownLatch> latches = new ConcurrentHashMap<>();

    public KafkaConsumerService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "OutTopic", groupId = "publisher-group")
    public void listenOutTopic(String rawMessage) {
        try {
            CommentKafkaMessage message = objectMapper.readValue(rawMessage, CommentKafkaMessage.class);
            log.info("Received from OutTopic: id={}, state={}", message.getId(), message.getState());

            responses.put(message.getId(), message);
            CountDownLatch latch = latches.get(message.getId());
            if (latch != null) {
                latch.countDown();
            }
        } catch (Exception e) {
            log.error("Error parsing OutTopic message: {}", e.getMessage(), e);
        }
    }

    public CommentKafkaMessage waitForResponse(Long id, long timeoutSeconds) {
        CountDownLatch latch = new CountDownLatch(1);
        latches.put(id, latch);
        try {
            boolean received = latch.await(timeoutSeconds, TimeUnit.SECONDS);
            if (received) return responses.remove(id);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            latches.remove(id);
        }
        return null;
    }
}