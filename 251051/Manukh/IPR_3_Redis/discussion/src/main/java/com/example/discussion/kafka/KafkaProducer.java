package com.example.discussion.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class KafkaProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String OUT_TOPIC = "OutTopic";

    public void sendReactionResponse(String id, String state) throws JsonProcessingException {
        Map<String, String> response = new HashMap<>();
        response.put("id", id);
        response.put("state", state);

        String message = objectMapper.writeValueAsString(response);

        kafkaTemplate.send(OUT_TOPIC, message);
        System.out.println("Sent response to OutTopic: id=" + id + ", state=" + state);
    }
}