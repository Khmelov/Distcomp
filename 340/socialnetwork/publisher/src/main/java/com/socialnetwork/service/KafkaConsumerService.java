package com.socialnetwork.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.socialnetwork.dto.kafka.KafkaMessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private static final String OUT_TOPIC = "OutTopic";

    @Autowired
    private KafkaMessageService kafkaMessageService;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = OUT_TOPIC)
    public void listen(String messageJson) {
        try {
            System.out.println("Received Kafka response JSON: " + messageJson);

            // Десериализуем JSON в объект
            KafkaMessageResponse response = objectMapper.readValue(messageJson, KafkaMessageResponse.class);

            System.out.println("Response RequestId: " + response.getRequestId());
            System.out.println("Response Success: " + response.isSuccess());

            if (response.getRequestId() != null) {
                kafkaMessageService.completeRequest(response.getRequestId(), response);
            } else {
                System.err.println("Invalid Kafka response received - no requestId");
            }
        } catch (Exception e) {
            System.err.println("Error processing Kafka response: " + e.getMessage());
            e.printStackTrace();
        }
    }
}