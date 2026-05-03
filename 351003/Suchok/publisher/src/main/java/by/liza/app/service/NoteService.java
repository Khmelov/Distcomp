package by.liza.app.service;

import by.liza.app.dto.request.NoteRequestTo;
import by.liza.app.dto.response.NoteResponseTo;
import by.liza.app.exception.DuplicateEntityException;
import by.liza.app.exception.EntityNotFoundException;
import by.liza.app.kafka.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteKafkaProducer kafkaProducer;
    private final NoteKafkaConsumer kafkaConsumer;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CACHE_PREFIX = "notes:";
    private static final String CACHE_ALL    = "notes_all";
    private static final Duration TTL        = Duration.ofMinutes(1);

    @Value("${kafka.response.timeout-ms:1000}")
    private long timeoutMs;

    public NoteResponseTo create(NoteRequestTo req) {
        long generatedId = System.currentTimeMillis();

        NoteKafkaMessage msg = NoteKafkaMessage.builder()
                .requestId(UUID.randomUUID().toString())
                .operation(NoteKafkaMessage.OperationType.CREATE)
                .articleId(req.getArticleId())
                .note(NoteKafkaDto.builder()
                        .id(generatedId)
                        .articleId(req.getArticleId())
                        .content(req.getContent())
                        .state("PENDING").build())
                .build();

        kafkaProducer.send(msg);

        NoteResponseTo response = new NoteResponseTo();
        response.setId(generatedId);
        response.setArticleId(req.getArticleId());
        response.setContent(req.getContent());
        response.setState("PENDING");

        // Кешируем сразу после создания
        redisTemplate.opsForValue().set(CACHE_PREFIX + generatedId, response, TTL);
        redisTemplate.delete(CACHE_ALL);

        return response;
    }

    public NoteResponseTo getById(Long id) {
        // Сначала ищем в кеше
        Object cached = redisTemplate.opsForValue().get(CACHE_PREFIX + id);
        if (cached instanceof NoteResponseTo note) {
            return note;
        }

        NoteKafkaMessage resp = sendAndWait(NoteKafkaMessage.builder()
                .requestId(UUID.randomUUID().toString())
                .operation(NoteKafkaMessage.OperationType.GET_BY_ID)
                .noteId(id).articleId(0L).build());
        checkError(resp, "Note with id " + id + " not found");

        NoteResponseTo result = toResponse(resp.getNote());
        redisTemplate.opsForValue().set(CACHE_PREFIX + id, result, TTL);
        return result;
    }

    @SuppressWarnings("unchecked")
    public List<NoteResponseTo> getAll() {
        // Ищем в кеше
        Object cached = redisTemplate.opsForValue().get(CACHE_ALL);
        if (cached instanceof List<?> list) {
            return (List<NoteResponseTo>) list;
        }

        NoteKafkaMessage resp = sendAndWait(NoteKafkaMessage.builder()
                .requestId(UUID.randomUUID().toString())
                .operation(NoteKafkaMessage.OperationType.GET_ALL)
                .articleId(0L).build());
        if (resp == null) throw new EntityNotFoundException("Discussion service timeout", 50001);

        List<NoteResponseTo> result = resp.getNoteList() != null
                ? resp.getNoteList().stream().map(this::toResponse).collect(Collectors.toList())
                : List.of();

        redisTemplate.opsForValue().set(CACHE_ALL, result, TTL);
        return result;
    }

    public NoteResponseTo update(NoteRequestTo req) {
        NoteKafkaMessage resp = sendAndWait(NoteKafkaMessage.builder()
                .requestId(UUID.randomUUID().toString())
                .operation(NoteKafkaMessage.OperationType.UPDATE)
                .articleId(req.getArticleId())
                .note(NoteKafkaDto.builder()
                        .id(req.getId()).articleId(req.getArticleId()).content(req.getContent()).build())
                .build());
        checkError(resp, "Note update failed");

        NoteResponseTo result = toResponse(resp.getNote());
        // Обновляем кеш
        redisTemplate.opsForValue().set(CACHE_PREFIX + result.getId(), result, TTL);
        redisTemplate.delete(CACHE_ALL);
        return result;
    }

    public void delete(Long id) {
        NoteKafkaMessage resp = sendAndWait(NoteKafkaMessage.builder()
                .requestId(UUID.randomUUID().toString())
                .operation(NoteKafkaMessage.OperationType.DELETE)
                .noteId(id).articleId(0L).build());
        checkError(resp, "Note with id " + id + " not found");

        // Инвалидируем кеш
        redisTemplate.delete(CACHE_PREFIX + id);
        redisTemplate.delete(CACHE_ALL);
    }

    private NoteKafkaMessage sendAndWait(NoteKafkaMessage req) {
        BlockingQueue<NoteKafkaMessage> q = kafkaConsumer.register(req.getRequestId());
        kafkaProducer.send(req);
        return poll(q, req.getRequestId());
    }

    private NoteKafkaMessage poll(BlockingQueue<NoteKafkaMessage> q, String reqId) {
        try { return q.poll(timeoutMs, TimeUnit.MILLISECONDS); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); return null; }
        finally { kafkaConsumer.unregister(reqId); }
    }

    private void checkError(NoteKafkaMessage resp, String fallback) {
        if (resp == null) throw new EntityNotFoundException("Discussion timeout", 50001);
        if (resp.getErrorCode() != null) {
            int c = resp.getErrorCode();
            String m = resp.getErrorMessage() != null ? resp.getErrorMessage() : fallback;
            if (c == 40404) throw new EntityNotFoundException(m, c);
            if (c == 40304) throw new DuplicateEntityException(m, c);
            throw new EntityNotFoundException(m, c);
        }
    }

    private NoteResponseTo toResponse(NoteKafkaDto dto) {
        NoteResponseTo r = new NoteResponseTo();
        r.setId(dto.getId());
        r.setArticleId(dto.getArticleId());
        r.setContent(dto.getContent());
        r.setState(dto.getState());
        return r;
    }
}