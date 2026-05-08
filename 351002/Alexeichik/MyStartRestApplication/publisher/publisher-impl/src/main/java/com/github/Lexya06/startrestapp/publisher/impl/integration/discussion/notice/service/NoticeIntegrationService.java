package com.github.Lexya06.startrestapp.publisher.impl.integration.discussion.notice.service;

import com.github.Lexya06.startrestapp.discussion.api.dto.notice.*;
import com.github.Lexya06.startrestapp.discussion.api.searchcriteria.implementation.NoticeSearchCriteria;
import com.github.Lexya06.startrestapp.publisher.impl.config.KafkaConfig;
import com.github.Lexya06.startrestapp.publisher.impl.service.customexception.MyEntitiesNotFoundException;
import com.github.Lexya06.startrestapp.publisher.impl.service.customexception.MyEntityNotFoundException;
import com.github.Lexya06.startrestapp.publisher.impl.service.realization.ArticleService;
import lombok.SneakyThrows;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class NoticeIntegrationService {
    private static final String CACHE_KEY_PREFIX = "notices::";
    
    private final ArticleService articleService;
    private final KafkaTemplate<Long, KafkaNoticeMessage> kafkaTemplate;
    private final ReplyingKafkaTemplate<Long, KafkaNoticeMessage, KafkaNoticeResponseMessage> replyingKafkaTemplate;
    private final JdbcTemplate jdbcTemplate;
    private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;


    @Autowired
    public NoticeIntegrationService(ArticleService articleService, 
                                     KafkaTemplate<Long, KafkaNoticeMessage> kafkaTemplate,
                                     ReplyingKafkaTemplate<Long, KafkaNoticeMessage, KafkaNoticeResponseMessage> replyingKafkaTemplate,
                                     JdbcTemplate jdbcTemplate,
                                     ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
        this.articleService = articleService;
        this.kafkaTemplate = kafkaTemplate;
        this.replyingKafkaTemplate = replyingKafkaTemplate;
        this.jdbcTemplate = jdbcTemplate;
        this.reactiveRedisTemplate = reactiveRedisTemplate;
    }

    public Mono<NoticeResponseTo> getById(NoticeKeyDto id) {
        String cacheKey = CACHE_KEY_PREFIX + id.toString();
        return reactiveRedisTemplate.opsForValue().get(cacheKey)
                .cast(NoticeResponseTo.class)
                .switchIfEmpty(Mono.defer(() -> {
                    KafkaNoticeMessage message = KafkaNoticeMessage.builder()
                            .operation(OperationType.GET)
                            .keyDto(id)
                            .build();
                    return sendAndReceive(message, id.getArticleId())
                            .flatMap(response -> reactiveRedisTemplate.opsForValue()
                                    .set(cacheKey, response, Duration.ofMinutes(60))
                                    .thenReturn(response));
                }));
    }

    public Mono<NoticeResponseTo> create(NoticeRequestTo requestDTO) {
        return Mono.fromCallable(() -> {
            try {
                articleService.validateExistence(requestDTO.getArticleId());
            } catch (MyEntityNotFoundException e) {
                throw e; // Preserve original 404 for article
            } catch (Exception e) {
                throw new RuntimeException("Error checking article existence: " + e.getMessage(), e);
            }
            return jdbcTemplate.queryForObject("SELECT nextval('notice_id_seq')", Long.class);
        })
        .flatMap(generatedId -> {
            KafkaNoticeMessage message = KafkaNoticeMessage.builder()
                    .operation(OperationType.POST)
                    .requestPayload(requestDTO)
                    .id(generatedId)
                    .build();

            return sendAndReceive(message, requestDTO.getArticleId())
                    .map(response -> NoticeResponseTo.builder()
                            .id(response.getId())
                            .articleId(response.getArticleId())
                            .country(response.getCountry())
                            .content(response.getContent())
                            .state(NoticeState.PENDING)
                            .build());
        })
        .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<NoticeResponseTo> update(NoticeKeyDto id, NoticeRequestTo requestDTO) {
        String cacheKey = CACHE_KEY_PREFIX + id.toString();
        KafkaNoticeMessage message = KafkaNoticeMessage.builder()
                .operation(OperationType.PUT)
                .keyDto(id)
                .requestPayload(requestDTO)
                .build();
        return sendAndReceive(message, id.getArticleId())
                .flatMap(response -> reactiveRedisTemplate.opsForValue().delete(cacheKey).thenReturn(response));
    }

    public Mono<Void> delete(NoticeKeyDto id) {
        String cacheKey = CACHE_KEY_PREFIX + id.toString();
        KafkaNoticeMessage message = KafkaNoticeMessage.builder()
                .operation(OperationType.DELETE)
                .keyDto(id)
                .build();
        return sendAndReceiveVoid(message, id.getArticleId())
                .flatMap(response -> reactiveRedisTemplate.opsForValue().delete(cacheKey).then());
    }

    public Mono<ResponseEntity<List<NoticeResponseTo>>> getAllByCriteria(NoticeSearchCriteria criteria) {
        KafkaNoticeMessage message = KafkaNoticeMessage.builder()
                .operation(OperationType.GET_ALL)
                .criteria(criteria)
                .build();
        return sendAndReceiveRaw(message, criteria.getArticleId())
                .handle((response, sink) -> {
                    if (response.getErrorType() != null) {
                        sink.error(mapError(response));
                        return;
                    }
                    sink.next(ResponseEntity.ok(response.getResponseListPayload()));
                });
    }

    public Mono<NoticeResponseTo> getByIdId(Long id) {
        String cacheKey = CACHE_KEY_PREFIX + "id_" + id;
        return reactiveRedisTemplate.opsForValue().get(cacheKey)
                .cast(NoticeResponseTo.class)
                .switchIfEmpty(Mono.defer(() -> {
                    KafkaNoticeMessage message = KafkaNoticeMessage.builder()
                            .operation(OperationType.GET)
                            .id(id)
                            .build();
                    return sendAndReceive(message, 0L)
                            .flatMap(response -> reactiveRedisTemplate.opsForValue()
                                    .set(cacheKey, response, Duration.ofMinutes(60))
                                    .thenReturn(response));
                }));
    }

    public Mono<NoticeResponseTo> updateByIdId(Long id, NoticeRequestTo requestDTO) {
        String cacheKey = CACHE_KEY_PREFIX + "id_" + id;
        KafkaNoticeMessage message = KafkaNoticeMessage.builder()
                .operation(OperationType.PUT)
                .id(id)
                .requestPayload(requestDTO)
                .build();
        return sendAndReceive(message, requestDTO.getArticleId())
                .flatMap(response -> reactiveRedisTemplate.opsForValue().delete(cacheKey).thenReturn(response));
    }

    public Mono<ResponseEntity<Void>> deleteByIdId(Long id) {
        String cacheKey = CACHE_KEY_PREFIX + "id_" + id;
        KafkaNoticeMessage message = KafkaNoticeMessage.builder()
                .operation(OperationType.DELETE)
                .id(id)
                .build();
        return sendAndReceiveVoid(message, 0L)
                .flatMap(response -> reactiveRedisTemplate.opsForValue().delete(cacheKey))
                .thenReturn(ResponseEntity.noContent().build());
    }

    private Mono<NoticeResponseTo> sendAndReceive(KafkaNoticeMessage message, Long articleId) {
        return sendAndReceiveRaw(message, articleId)
                .flatMap(response -> {
                    if (response.getErrorType() != null) {
                        return Mono.error(mapError(response));
                    }
                    return Mono.justOrEmpty(response.getResponsePayload());
                });
    }

    private Mono<Void> sendAndReceiveVoid(KafkaNoticeMessage message, Long articleId) {
        return sendAndReceiveRaw(message, articleId)
                .flatMap(response -> {
                    if (response.getErrorType() != null) {
                        return Mono.error(mapError(response));
                    }
                    return Mono.empty();
                });
    }

    private Mono<KafkaNoticeResponseMessage> sendAndReceiveRaw(KafkaNoticeMessage message, Long articleId) {
        Long key = (articleId == null) ? 0L : articleId;
        ProducerRecord<Long, KafkaNoticeMessage> record = new ProducerRecord<>(KafkaConfig.IN_TOPIC, null, key, message);
        RequestReplyFuture<Long, KafkaNoticeMessage, KafkaNoticeResponseMessage> future = replyingKafkaTemplate.sendAndReceive(record);

        return Mono.fromFuture(future.toCompletableFuture())
                .timeout(Duration.ofMillis(1000))
                .map(ConsumerRecord::value);
    }

    private RuntimeException mapError(KafkaNoticeResponseMessage response) {
        String errorType = response.getErrorType();
        String message = response.getErrorMessage();
        String errorKey = response.getErrorKey();

        if ("MyEntityNotFoundException".equals(errorType)) {
            Long id = null;
            try {
                if (errorKey != null) {
                    id = Long.valueOf(errorKey);
                }
            } catch (NumberFormatException e) {
                // Ignore
            }
            return new MyEntityNotFoundException(id, NoticeRequestTo.class);
        } else if ("MyEntitiesNotFoundException".equals(errorType)) {
            return new MyEntitiesNotFoundException(Collections.emptyList(), NoticeRequestTo.class);
        } else {
            return new RuntimeException("Kafka error from discussion service: [" + errorType + "] - " + message);
        }
    }}
