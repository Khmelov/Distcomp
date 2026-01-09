package org.example.task340.publisher.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class KafkaMessageProducer {

    private static final Logger log = LoggerFactory.getLogger(KafkaMessageProducer.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.in:InTopic}")
    private String inTopic;

    public KafkaMessageProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public String sendMessage(KafkaMessageRequest request) {
        String requestId = UUID.randomUUID().toString();
        request.setRequestId(requestId);

        // Use tweetId as key to ensure messages for same tweet go to same partition
        String key = request.getTweetId() != null ? request.getTweetId().toString() : requestId;

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(inTopic, key, request);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent message=[{}] with offset=[{}]", request, result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send message=[{}] due to : {}", request, ex.getMessage());
            }
        });

        return requestId;
    }
}

