package com.example.publisher.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    @Autowired
    private ObjectMapper objectMapper;

    private static final String OUT_TOPIC = "OutTopic";
    private static final String GROUP_ID = "publisher-group";

    @KafkaListener(topics = OUT_TOPIC, groupId = GROUP_ID)
    public void consume(String message) {
        try {
            JsonNode json = objectMapper.readTree(message);
            String id = json.get("id").asText();
            String state = json.get("state").asText();

            System.out.println("Received reaction response: id=" + id +
                    ", state=" + state);

            // Здесь можно обновить статус реакции в базе данных
            // или отправить уведомление

        } catch (Exception e) {
            System.err.println("Error processing Kafka message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}