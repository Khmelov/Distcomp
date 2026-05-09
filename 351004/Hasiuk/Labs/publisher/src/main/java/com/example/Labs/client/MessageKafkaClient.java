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
import org.springframework.cache.annotation.Caching;
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

    // Кешируем только GET запросы. Если идет UPDATE - сбрасываем кеш.
    @Caching(
            evict = { @CacheEvict(value = "messages", key = "#id", condition = "#op == 'UPDATE'") },
            cacheable = { @Cacheable(value = "messages", key = "#id", condition = "#op == 'GET_BY_ID'") }
    )
    public MessageResponseTo sendAndReceive(String op, Long id, MessageRequestTo req) {
        try {
            String key = (req != null) ? req.getStoryId().toString() : (id != null ? id.toString() : "0");
            String payload = (req != null) ? objectMapper.writeValueAsString(req) : "";

            ProducerRecord<String, String> record = new ProducerRecord<>("InTopic", key, payload);
            record.headers().add("op", op.getBytes());
            if (id != null) record.headers().add("id", id.toString().getBytes());

            RequestReplyFuture<String, String, String> future = kafkaTemplate.sendAndReceive(record, Duration.ofSeconds(5));
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

    // Тот самый метод getAll, которого не хватало контроллеру
    public List<MessageResponseTo> getAll() {
        try {
            ProducerRecord<String, String> record = new ProducerRecord<>("InTopic", "0", "");
            record.headers().add("op", "GET_ALL".getBytes());

            RequestReplyFuture<String, String, String> future = kafkaTemplate.sendAndReceive(record, Duration.ofSeconds(5));
            String resultStr = future.get().value();

            if (resultStr != null && resultStr.startsWith("ERROR")) throw new RuntimeException(resultStr);

            return objectMapper.readValue(resultStr, new TypeReference<List<MessageResponseTo>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Kafka error: " + e.getMessage());
        }
    }

    // Метод для удаления, который вызывается из контроллера и чистит Redis
    @CacheEvict(value = "messages", key = "#id")
    public void deleteViaKafka(Long id) {
        sendAndReceive("DELETE", id, null);
    }
}