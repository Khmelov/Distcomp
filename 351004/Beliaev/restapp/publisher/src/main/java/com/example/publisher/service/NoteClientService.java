package com.example.publisher.service;

import com.example.publisher.dto.kafka.KafkaNoteRequest;
import com.example.publisher.dto.kafka.KafkaNoteResponse;
import com.example.publisher.dto.request.NoteRequestTo;
import com.example.publisher.dto.response.NoteResponseTo;
import com.example.publisher.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class NoteClientService {

    private final ReplyingKafkaTemplate<String, KafkaNoteRequest, KafkaNoteResponse> replyingTemplate;

    @Caching(evict = @CacheEvict(value = "notes_list", allEntries = true))
    public NoteResponseTo create(NoteRequestTo request) {
        Long generatedId = Math.abs(UUID.randomUUID().getMostSignificantBits());
        KafkaNoteRequest kafkaReq = new KafkaNoteRequest("CREATE", null, request, generatedId);
        ProducerRecord<String, KafkaNoteRequest> record =
                new ProducerRecord<>("InTopic", String.valueOf(request.getArticleId()), kafkaReq);
        replyingTemplate.send(record); // Асинхронно

        NoteResponseTo response = new NoteResponseTo();
        response.setId(generatedId);
        response.setArticleId(request.getArticleId());
        response.setContent(request.getContent());
        response.setState("PENDING");
        return response;
    }

    @Cacheable(value = "notes_list")
    public List<NoteResponseTo> getAll() {
        KafkaNoteRequest req = new KafkaNoteRequest("GET_ALL", null, null, null);
        return sendAndWait(req, "GET_ALL_KEY").getNotes();
    }

    // Если данные есть в кэше Redis, Kafka не будет вызвана!
    @Cacheable(value = "note", key = "#id")
    public NoteResponseTo getById(Long id) {
        KafkaNoteRequest req = new KafkaNoteRequest("GET", id, null, null);
        return sendAndWait(req, String.valueOf(id)).getNote();
    }

    @Caching(
            put = @CachePut(value = "note", key = "#id"),
            evict = @CacheEvict(value = "notes_list", allEntries = true)
    )
    public NoteResponseTo update(Long id, NoteRequestTo request) {
        KafkaNoteRequest req = new KafkaNoteRequest("UPDATE", id, request, null);
        return sendAndWait(req, String.valueOf(request.getArticleId())).getNote();
    }

    @Caching(evict = {
            @CacheEvict(value = "note", key = "#id"),
            @CacheEvict(value = "notes_list", allEntries = true)
    })
    public void delete(Long id) {
        KafkaNoteRequest req = new KafkaNoteRequest("DELETE", id, null, null);
        sendAndWait(req, String.valueOf(id));
    }

    private KafkaNoteResponse sendAndWait(KafkaNoteRequest request, String partitionKey) {
        ProducerRecord<String, KafkaNoteRequest> record = new ProducerRecord<>("InTopic", partitionKey, request);
        RequestReplyFuture<String, KafkaNoteRequest, KafkaNoteResponse> replyFuture = replyingTemplate.sendAndReceive(record);
        try {
            ConsumerRecord<String, KafkaNoteResponse> responseRecord = replyFuture.get(1, TimeUnit.SECONDS);
            KafkaNoteResponse response = responseRecord.value();
            if (response.getError() != null) {
                throw new EntityNotFoundException(response.getError());
            }
            return response;
        } catch (TimeoutException e) {
            throw new RuntimeException("Timeout error: Сервис discussion не ответил за 1 секунду");
        } catch (Exception e) {
            throw new RuntimeException("Kafka error: " + e.getMessage());
        }
    }
}