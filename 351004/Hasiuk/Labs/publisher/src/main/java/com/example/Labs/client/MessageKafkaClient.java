package com.example.Labs.client;

import com.example.Labs.dto.request.MessageRequestTo;
import com.example.Labs.dto.response.MessageResponseTo;
import com.example.Labs.exception.ResourceNotFoundException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MessageKafkaClient {
    private final ReplyingKafkaTemplate<String, String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public MessageResponseTo sendAndReceive(String op, Long id, MessageRequestTo req) {
        try {
            String key = req != null ? req.getStoryId().toString() : (id != null ? id.toString() : "0");
            String data = req != null ? objectMapper.writeValueAsString(req) : "";
            ProducerRecord<String, String> record = new ProducerRecord<>("InTopic", key, data);
            record.headers().add("op", op.getBytes());
            if (id != null) record.headers().add("id", id.toString().getBytes());

            RequestReplyFuture<String, String, String> future = kafkaTemplate.sendAndReceive(record, Duration.ofSeconds(5));
            String val = future.get().value();

            if (val != null) {
                if (val.startsWith("ERROR:404")) throw new ResourceNotFoundException("Not Found");
                if (val.startsWith("ERROR")) throw new IllegalArgumentException(val);
            }

            if (val == null || val.equals("DELETED")) return null;
            return objectMapper.readValue(val, MessageResponseTo.class);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Kafka error: " + e.getMessage());
        }
    }

    public List<MessageResponseTo> getAll() {
        try {
            ProducerRecord<String, String> record = new ProducerRecord<>("InTopic", "0", "");
            record.headers().add("op", "GET_ALL".getBytes());
            String val = kafkaTemplate.sendAndReceive(record, Duration.ofSeconds(5)).get().value();
            return objectMapper.readValue(val, new TypeReference<List<MessageResponseTo>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Kafka error: " + e.getMessage());
        }
    }
}