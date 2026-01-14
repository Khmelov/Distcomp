package com.socialnetwork.service;

import com.socialnetwork.dto.kafka.KafkaMessageRequest;
import com.socialnetwork.dto.kafka.KafkaMessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducerService {

    private static final String IN_TOPIC = "InTopic";

    @Autowired
    private KafkaTemplate<String, KafkaMessageRequest> kafkaTemplate;

    @Autowired
    private KafkaMessageService kafkaMessageService;

    public CompletableFuture<KafkaMessageResponse> sendMessageRequest(KafkaMessageRequest request) {
        String key = kafkaMessageService.getPartitionKey(request.getTweetId());

        System.out.println("Sending Kafka request to InTopic");
        System.out.println("RequestId: " + request.getRequestId());
        System.out.println("Operation: " + request.getOperation());
        System.out.println("Key: " + key);

        CompletableFuture<SendResult<String, KafkaMessageRequest>> sendFuture =
                kafkaTemplate.send(IN_TOPIC, key, request);

        CompletableFuture<KafkaMessageResponse> responseFuture =
                kafkaMessageService.waitForResponse(request.getRequestId());

        return sendFuture.thenCompose(result -> {
            System.out.println("Kafka message sent successfully");
            return responseFuture;
        });
    }
}