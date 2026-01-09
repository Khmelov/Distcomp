package org.example.task340.publisher.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
public class KafkaMessageConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaMessageConsumer.class);

    private final Map<String, KafkaMessageResponse> responseMap = new ConcurrentHashMap<>();
    private final Map<String, CountDownLatch> latchMap = new ConcurrentHashMap<>();

    @KafkaListener(topics = "${kafka.topic.out:OutTopic}", groupId = "${spring.kafka.consumer.group-id:publisher-group}")
    public void consume(@Payload KafkaMessageResponse response,
                       @Header(KafkaHeaders.RECEIVED_KEY) String key,
                       Acknowledgment acknowledgment) {
        log.info("Received response: requestId={}, operation={}", response.getRequestId(), response.getOperation());

        String requestId = response.getRequestId();
        if (requestId != null) {
            responseMap.put(requestId, response);
            CountDownLatch latch = latchMap.remove(requestId);
            if (latch != null) {
                latch.countDown();
            }
        }

        if (acknowledgment != null) {
            acknowledgment.acknowledge();
        }
    }

    public KafkaMessageResponse waitForResponse(String requestId, long timeoutSeconds) {
        try {
            // Check if response already received
            KafkaMessageResponse existing = responseMap.get(requestId);
            if (existing != null) {
                return responseMap.remove(requestId);
            }

            CountDownLatch latch = new CountDownLatch(1);
            latchMap.put(requestId, latch);

            boolean received = latch.await(timeoutSeconds, TimeUnit.SECONDS);
            if (!received) {
                log.warn("Timeout waiting for response: requestId={}", requestId);
                latchMap.remove(requestId);
                return null;
            }

            return responseMap.remove(requestId);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while waiting for response: requestId={}", requestId, e);
            latchMap.remove(requestId);
            return null;
        }
    }
}

