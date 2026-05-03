package com.example.publisher.controller;

import com.example.publisher.dto.kafka.KafkaNoteRequest;
import com.example.publisher.dto.kafka.KafkaNoteResponse;
import com.example.publisher.dto.request.NoteRequestTo;
import com.example.publisher.dto.response.NoteResponseTo;
import com.example.publisher.exception.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/api/v1.0/notes")
@RequiredArgsConstructor
public class NoteController {

    private final ReplyingKafkaTemplate<String, KafkaNoteRequest, KafkaNoteResponse> replyingTemplate;

    @PostMapping
    public ResponseEntity<NoteResponseTo> create(@Valid @RequestBody NoteRequestTo request) {
        // Генерируем ID на стороне Publisher (имитация ID базы данных)
        Long generatedId = Math.abs(UUID.randomUUID().getMostSignificantBits());

        KafkaNoteRequest kafkaReq = new KafkaNoteRequest("CREATE", null, request, generatedId);

        // Отправляем асинхронно. Ключ = articleId, чтобы всё уходило в одну партицию
        ProducerRecord<String, KafkaNoteRequest> record =
                new ProducerRecord<>("InTopic", String.valueOf(request.getArticleId()), kafkaReq);
        replyingTemplate.send(record); // Обычный send, не ждем ответа

        // Возвращаем клиенту немедленно
        NoteResponseTo response = new NoteResponseTo();
        response.setId(generatedId);
        response.setArticleId(request.getArticleId());
        response.setContent(request.getContent());
        response.setState("PENDING");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public List<NoteResponseTo> getAll() {
        KafkaNoteRequest req = new KafkaNoteRequest("GET_ALL", null, null, null);
        return sendAndWait(req, "GET_ALL_KEY").getNotes();
    }

    @GetMapping("/{id}")
    public NoteResponseTo getById(@PathVariable Long id) {
        KafkaNoteRequest req = new KafkaNoteRequest("GET", id, null, null);
        return sendAndWait(req, String.valueOf(id)).getNote();
    }

    @PutMapping("/{id}")
    public NoteResponseTo update(@PathVariable Long id, @Valid @RequestBody NoteRequestTo request) {
        KafkaNoteRequest req = new KafkaNoteRequest("UPDATE", id, request, null);
        return sendAndWait(req, String.valueOf(request.getArticleId())).getNote();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        KafkaNoteRequest req = new KafkaNoteRequest("DELETE", id, null, null);
        sendAndWait(req, String.valueOf(id));
    }

    // Вспомогательный метод для Request-Reply операций с таймаутом
    private KafkaNoteResponse sendAndWait(KafkaNoteRequest request, String partitionKey) {
        ProducerRecord<String, KafkaNoteRequest> record = new ProducerRecord<>("InTopic", partitionKey, request);
        RequestReplyFuture<String, KafkaNoteRequest, KafkaNoteResponse> replyFuture = replyingTemplate.sendAndReceive(record);

        try {
            // Ждем ответ максимум 1 секунду
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