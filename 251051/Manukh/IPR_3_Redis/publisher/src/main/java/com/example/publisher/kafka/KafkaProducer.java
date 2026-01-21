package com.example.publisher.kafka;

import com.example.publisher.dto.KafkaReactionRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class KafkaProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String IN_TOPIC = "InTopic";

    public void sendReactionRequest(KafkaReactionRequest request) throws JsonProcessingException {
        String message = objectMapper.writeValueAsString(request);

        // Отправляем в партицию по storyId для гарантии порядка
        int partition = Math.abs(request.getStoryId().hashCode()) % 3; // 3 partitions

        CompletableFuture<SendResult<String, String>> future =
                kafkaTemplate.send(IN_TOPIC, partition, request.getStoryId().toString(), message);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                System.out.println("Sent message=[" + message +
                        "] with offset=[" + result.getRecordMetadata().offset() + "]");
            } else {
                System.out.println("Unable to send message=[" +
                        message + "] due to : " + ex.getMessage());
            }
        });
    }
}