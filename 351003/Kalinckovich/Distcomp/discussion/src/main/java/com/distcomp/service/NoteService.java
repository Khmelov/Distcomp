package com.distcomp.service;

import com.distcomp.config.kafka.KafkaCorrelationManager;
import com.distcomp.dto.note.NoteCreateRequest;
import com.distcomp.dto.note.NotePatchRequest;
import com.distcomp.dto.note.NoteResponseDto;
import com.distcomp.dto.note.NoteUpdateRequest;
import com.distcomp.errorhandling.exceptions.BusinessValidationException;
import com.distcomp.errorhandling.model.ValidationError;
import com.distcomp.event.note.NoteInboundEvent;
import com.distcomp.event.note.NoteOutboundEvent;
import com.distcomp.publisher.abstraction.KafkaEventPublisher;
import com.distcomp.config.kafka.KafkaTopic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service("cassandraNoteService")
@RequiredArgsConstructor
public class NoteService {

    private static final String DEFAULT_COUNTRY = "default";
    private static final long KAFKA_RESPONSE_TIMEOUT_SECONDS = 30;

    private final KafkaEventPublisher kafkaEventPublisher;
    private final KafkaCorrelationManager correlationManager;

    public Mono<NoteResponseDto> findById(final Long topicId, final Long noteId) {
        return fetchNoteFromKafka(correlationId -> NoteInboundEvent.findById(correlationId, topicId, noteId));
    }

    public Mono<NoteResponseDto> findById(final Long id) {
        return fetchNoteFromKafka(correlationId -> NoteInboundEvent.findById(correlationId, id));
    }

    public Flux<NoteResponseDto> findAllByTopicId(final Long topicId, final int page, final int size) {
        return fetchListFromKafka(correlationId -> NoteInboundEvent.findAll(correlationId, topicId, page, size));
    }

    public Flux<NoteResponseDto> findAll(final int page, final int size) {
        return fetchListFromKafka(correlationId -> NoteInboundEvent.findAll(correlationId, page, size));
    }

    public Mono<NoteResponseDto> create(final NoteCreateRequest request) {
        final Long generatedId = IdGenerator.nextId();
        return fetchNoteFromKafka(correlationId -> NoteInboundEvent.create(correlationId, request, generatedId, DEFAULT_COUNTRY));
    }

    public Mono<NoteResponseDto> update(final Long topicId, final Long noteId, final NoteUpdateRequest request) {
        return fetchNoteFromKafka(correlationId -> NoteInboundEvent.update(correlationId, topicId, noteId, request, DEFAULT_COUNTRY));
    }

    public Mono<NoteResponseDto> update(final Long id, final NoteUpdateRequest request) {
        return fetchNoteFromKafka(correlationId -> NoteInboundEvent.update(correlationId, id, request));
    }

    public Mono<NoteResponseDto> patch(final Long topicId, final Long noteId, final NotePatchRequest request) {
        return fetchNoteFromKafka(correlationId -> NoteInboundEvent.patch(correlationId, topicId, noteId, request, DEFAULT_COUNTRY));
    }

    public Mono<NoteResponseDto> patch(final Long id, final NotePatchRequest request) {
        return fetchNoteFromKafka(correlationId -> NoteInboundEvent.patch(correlationId, id, request));
    }

    public Mono<Void> delete(final Long topicId, final Long noteId) {
        return executeVoidKafka(correlationId -> NoteInboundEvent.delete(correlationId, topicId, noteId));
    }

    public Mono<Void> delete(final Long id) {
        return executeVoidKafka(correlationId -> NoteInboundEvent.delete(correlationId, id));
    }

    public Mono<Void> deleteByTopicId(final Long topicId) {
        return executeVoidKafka(correlationId -> NoteInboundEvent.deleteByTopicId(correlationId, topicId));
    }

    private Mono<NoteResponseDto> fetchNoteFromKafka(final NoteEventFactory eventFactory) {
        return Mono.fromCallable(() -> {
                    final String correlationId = correlationManager.registerRequest();
                    kafkaEventPublisher.publish(KafkaTopic.IN_TOPIC, correlationId, eventFactory.create(correlationId));
                    return waitForNoteResponse(correlationId);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .doOnError(e -> log.error("Failed to fetch note from Kafka", e));
    }

    private Flux<NoteResponseDto> fetchListFromKafka(final NoteListEventFactory eventFactory) {
        return Mono.fromCallable(() -> {
                    final String correlationId = correlationManager.registerRequest();
                    kafkaEventPublisher.publish(KafkaTopic.IN_TOPIC, correlationId, eventFactory.create(correlationId));
                    return waitForNoteListResponse(correlationId);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .doOnError(e -> log.error("Failed to fetch note list from Kafka", e));
    }

    private Mono<Void> executeVoidKafka(final VoidEventFactory eventFactory) {
        return Mono.fromRunnable(() -> {
                    final String correlationId = correlationManager.registerRequest();
                    kafkaEventPublisher.publish(KafkaTopic.IN_TOPIC, correlationId, eventFactory.create(correlationId));
                    waitForVoidResponse(correlationId);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .doOnError(e -> log.error("Failed to execute void Kafka operation", e)).then();
    }

    private NoteResponseDto waitForNoteResponse(final String correlationId) {
        try {
            final Object response = correlationManager.waitForResponse(correlationId, Object.class, KAFKA_RESPONSE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (response instanceof final NoteOutboundEvent outbound) {
                if (outbound.getStatus() == NoteOutboundEvent.OperationStatus.FAILURE) {
                    throw new BusinessValidationException(List.of(ValidationError.builder().field("kafka").message(outbound.getMessage()).build()));
                }
                return convertToNoteResponseDto(outbound);
            }
            throw new IllegalStateException("Unexpected response type: " + response.getClass());
        } catch (final BusinessValidationException e) {
            throw e;
        } catch (final Exception e) {
            log.error("Failed to wait for Kafka response: {}", correlationId, e);
            throw new BusinessValidationException(List.of(ValidationError.builder().field("kafka").message("Failed to process request: " + e.getMessage()).build()));
        }
    }

    private List<NoteResponseDto> waitForNoteListResponse(final String correlationId) {
        try {
            final Object response = correlationManager.waitForResponse(correlationId, Object.class, KAFKA_RESPONSE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (response instanceof final NoteOutboundEvent outbound) {
                if (outbound.getStatus() == NoteOutboundEvent.OperationStatus.FAILURE) {
                    throw new BusinessValidationException(List.of(ValidationError.builder().field("kafka").message(outbound.getMessage()).build()));
                }
                return outbound.getResponseList().stream().map(this::convertToNoteResponseDto).toList();
            }
            throw new IllegalStateException("Unexpected response type: " + response.getClass());
        } catch (final BusinessValidationException e) {
            throw e;
        } catch (final Exception e) {
            log.error("Failed to wait for Kafka response: {}", correlationId, e);
            throw new BusinessValidationException(List.of(ValidationError.builder().field("kafka").message("Failed to process request: " + e.getMessage()).build()));
        }
    }

    private void waitForVoidResponse(final String correlationId) {
        try {
            correlationManager.waitForResponse(correlationId, Object.class, KAFKA_RESPONSE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (final BusinessValidationException e) {
            throw e;
        } catch (final Exception e) {
            log.error("Failed to wait for Kafka response: {}", correlationId, e);
            throw new BusinessValidationException(List.of(ValidationError.builder().field("kafka").message("Failed to process request: " + e.getMessage()).build()));
        }
    }

    private NoteResponseDto convertToNoteResponseDto(final NoteOutboundEvent.NoteResponseData data) {
        return NoteResponseDto.builder().id(data.getId()).topicId(data.getTopicId()).country(data.getCountry()).content(data.getContent()).build();
    }

    private NoteResponseDto convertToNoteResponseDto(final NoteOutboundEvent outbound) {
        return outbound.getResponseData() == null ? null : convertToNoteResponseDto(outbound.getResponseData());
    }

    @FunctionalInterface
    private interface NoteEventFactory { NoteInboundEvent create(String correlationId); }
    @FunctionalInterface
    private interface NoteListEventFactory { NoteInboundEvent create(String correlationId); }
    @FunctionalInterface
    private interface VoidEventFactory { NoteInboundEvent create(String correlationId); }
}