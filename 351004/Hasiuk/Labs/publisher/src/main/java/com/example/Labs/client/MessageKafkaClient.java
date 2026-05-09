package com.example.Labs.client;

import com.example.Labs.dto.request.MessageRequestTo;
import com.example.Labs.dto.response.MessageResponseTo;
import com.example.Labs.exception.ResourceNotFoundException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
            String key = (req != null) ? req.getStoryId().toString() : (id != null ? id.toString() : "0");
            String payload = (req != null) ? objectMapper.writeValueAsString(req) : "";

            ProducerRecord<String, String> record = new ProducerRecord<>("InTopic", key, payload);
            record.headers().add("op", op.getBytes());
            if (id != null) record.headers().add("id", id.toString().getBytes());

            RequestReplyFuture<String, String, String> future =
                    kafkaTemplate.sendAndReceive(record, Duration.ofSeconds(5));
            String resultStr = future.get().value();

            if (resultStr != null) {
                if (resultStr.startsWith("ERROR:404")) throw new ResourceNotFoundException("Not Found");
                if (resultStr.startsWith("ERROR")) throw new IllegalArgumentException(resultStr);
            }

            if (resultStr == null || resultStr.equals("DELETED")) return null;
            return objectMapper.readValue(resultStr, MessageResponseTo.class);

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Kafka error: " + e.getMessage());
        }
    }

    @Cacheable(value = "messages", key = "#id")
    public MessageResponseTo getByIdCached(Long id) {
        return sendAndReceive("GET_BY_ID", id, null);
    }

    @CacheEvict(value = "messages", key = "#id")
    public MessageResponseTo updateViaKafka(Long id, MessageRequestTo req) {
        return sendAndReceive("UPDATE", id, req);
    }

    public List<MessageResponseTo> getAll() {
        try {
            ProducerRecord<String, String> record = new ProducerRecord<>("InTopic", "0", "");
            record.headers().add("op", "GET_ALL".getBytes());

            RequestReplyFuture<String, String, String> future =
                    kafkaTemplate.sendAndReceive(record, Duration.ofSeconds(5));
            String resultStr = future.get().value();

            if (resultStr != null && resultStr.startsWith("ERROR")) throw new RuntimeException(resultStr);

            return objectMapper.readValue(resultStr, new TypeReference<List<MessageResponseTo>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Kafka error: " + e.getMessage());
        }
    }

    @CacheEvict(value = "messages", key = "#id")
    public void deleteViaKafka(Long id) {
        sendAndReceive("DELETE", id, null);
    }
}