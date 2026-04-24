package com.distcomp.service;

import com.distcomp.config.kafka.KafkaCorrelationManager;
import com.distcomp.dto.note.NoteCreateRequest;
import com.distcomp.dto.note.NotePatchRequest;
import com.distcomp.dto.note.NoteResponseDto;
import com.distcomp.dto.note.NoteUpdateRequest;
import com.distcomp.errorhandling.exceptions.BusinessValidationException;
import com.distcomp.errorhandling.exceptions.NoteNotFoundException;
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
        return Mono.fromCallable(() -> {
            final String correlationId = correlationManager.registerRequest();

            final NoteInboundEvent event = NoteInboundEvent.findById(correlationId, topicId, noteId);
            kafkaEventPublisher.publish(KafkaTopic.IN_TOPIC, correlationId, event);

            return waitForNoteResponse(correlationId);
        }).subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic());
    }

    public Flux<NoteResponseDto> findAllByTopicId(final Long topicId, final int page, final int size) {
        return Mono.fromCallable(() -> {
                    final String correlationId = correlationManager.registerRequest();

                    final NoteInboundEvent event = NoteInboundEvent.findAll(correlationId, topicId, page, size);
                    kafkaEventPublisher.publish(KafkaTopic.IN_TOPIC, correlationId, event);

                    return waitForNoteListResponse(correlationId);
                }).subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable);
    }

    public Mono<NoteResponseDto> create(final NoteCreateRequest request) {
        return Mono.fromCallable(() -> {
            final String correlationId = correlationManager.registerRequest();
            final Long generatedId = IdGenerator.nextId();

            final NoteInboundEvent event = NoteInboundEvent.create(correlationId, request, generatedId, DEFAULT_COUNTRY);
            kafkaEventPublisher.publish(KafkaTopic.IN_TOPIC, correlationId, event);

            
            return NoteResponseDto.builder()
                    .id(generatedId)
                    .topicId(request.getTopicId())
                    .country(DEFAULT_COUNTRY)
                    .content(request.getContent())
                    .build();
        }).subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic());
    }

    public Mono<NoteResponseDto> update(final Long topicId, final Long noteId, final NoteUpdateRequest request) {
        return Mono.fromCallable(() -> {
            final String correlationId = correlationManager.registerRequest();

            final NoteInboundEvent event = NoteInboundEvent.update(correlationId, topicId, noteId, request, DEFAULT_COUNTRY);
            kafkaEventPublisher.publish(KafkaTopic.IN_TOPIC, correlationId, event);

            return waitForNoteResponse(correlationId);
        }).subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic());
    }

    public Mono<NoteResponseDto> patch(final Long topicId, final Long noteId, final NotePatchRequest request) {
        return Mono.fromCallable(() -> {
            final String correlationId = correlationManager.registerRequest();

            final NoteInboundEvent event = NoteInboundEvent.patch(correlationId, topicId, noteId, request, DEFAULT_COUNTRY);
            kafkaEventPublisher.publish(KafkaTopic.IN_TOPIC, correlationId, event);

            return waitForNoteResponse(correlationId);
        }).subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic());
    }

    public Mono<Void> delete(final Long topicId, final Long noteId) {
        return Mono.fromRunnable(() -> {
                    final String correlationId = correlationManager.registerRequest();

                    final NoteInboundEvent event = NoteInboundEvent.delete(correlationId, topicId, noteId);
                    kafkaEventPublisher.publish(KafkaTopic.IN_TOPIC, correlationId, event);

                    waitForVoidResponse(correlationId);
                }).subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic())
                .then();
    }

    

    public Mono<NoteResponseDto> findById(final Long id) {
        return Mono.fromCallable(() -> {
            final String correlationId = correlationManager.registerRequest();

            final NoteInboundEvent event = NoteInboundEvent.findById(correlationId, id);
            kafkaEventPublisher.publish(KafkaTopic.IN_TOPIC, correlationId, event);

            return waitForNoteResponse(correlationId);
        }).subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic());
    }

    public Flux<NoteResponseDto> findAll(final int page, final int size) {
        return Mono.fromCallable(() -> {
                    final String correlationId = correlationManager.registerRequest();

                    final NoteInboundEvent event = NoteInboundEvent.findAll(correlationId, page, size);
                    kafkaEventPublisher.publish(KafkaTopic.IN_TOPIC, correlationId, event);

                    return waitForNoteListResponse(correlationId);
                }).subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable);
    }

    public Mono<NoteResponseDto> update(final Long id, final NoteUpdateRequest request) {
        return Mono.fromCallable(() -> {
            final String correlationId = correlationManager.registerRequest();

            final NoteInboundEvent event = NoteInboundEvent.update(correlationId, id, request);
            kafkaEventPublisher.publish(KafkaTopic.IN_TOPIC, correlationId, event);

            return waitForNoteResponse(correlationId);
        }).subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic());
    }

    public Mono<NoteResponseDto> patch(final Long id, final NotePatchRequest request) {
        return Mono.fromCallable(() -> {
            final String correlationId = correlationManager.registerRequest();

            final NoteInboundEvent event = NoteInboundEvent.patch(correlationId, id, request);
            kafkaEventPublisher.publish(KafkaTopic.IN_TOPIC, correlationId, event);

            return waitForNoteResponse(correlationId);
        }).subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic());
    }

    public Mono<Void> delete(final Long id) {
        return Mono.fromRunnable(() -> {
                    final String correlationId = correlationManager.registerRequest();

                    final NoteInboundEvent event = NoteInboundEvent.delete(correlationId, id);
                    kafkaEventPublisher.publish(KafkaTopic.IN_TOPIC, correlationId, event);

                    waitForVoidResponse(correlationId);
                }).subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic())
                .then();
    }

    public Mono<Void> deleteByTopicId(final Long topicId) {
        return Mono.fromRunnable(() -> {
                    final String correlationId = correlationManager.registerRequest();

                    final NoteInboundEvent event = NoteInboundEvent.deleteByTopicId(correlationId, topicId);
                    kafkaEventPublisher.publish(KafkaTopic.IN_TOPIC, correlationId, event);

                    waitForVoidResponse(correlationId);
                }).subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic())
                .then();
    }

    

    private NoteResponseDto waitForNoteResponse(final String correlationId) {
        try {
            final Object response = correlationManager.waitForResponse(
                    correlationId,
                    Object.class,
                    KAFKA_RESPONSE_TIMEOUT_SECONDS,
                    TimeUnit.SECONDS
            );

            if (response instanceof final NoteOutboundEvent outbound) {
                
                if (outbound.getStatus() == NoteOutboundEvent.OperationStatus.FAILURE) {
                    throw new BusinessValidationException(
                            List.of(
                                    ValidationError.builder()
                                            .field("kafka")
                                            .message(outbound.getMessage())
                                            .build()
                            )
                    );
                }
                return convertToNoteResponseDto(outbound);
            }

            throw new IllegalStateException("Unexpected response type: " + response.getClass());

        } catch (final NoteNotFoundException e) {
            
            throw e;
        } catch (final BusinessValidationException e) {
            
            throw e;
        } catch (final Exception e) {
            log.error("Failed to wait for Kafka response: {}", correlationId, e);
            
            throw new BusinessValidationException(
                    List.of(
                            ValidationError.builder()
                                    .field("kafka")
                                    .message("Failed to process request: " + e.getMessage())
                                    .build()
                    )
            );
        }
    }

    private List<NoteResponseDto> waitForNoteListResponse(final String correlationId) {
        try {
            final Object response = correlationManager.waitForResponse(
                    correlationId,
                    Object.class,
                    KAFKA_RESPONSE_TIMEOUT_SECONDS,
                    TimeUnit.SECONDS
            );

            if (response instanceof final NoteOutboundEvent outbound) {
                if (outbound.getStatus() == NoteOutboundEvent.OperationStatus.FAILURE) {
                    throw new BusinessValidationException(
                            List.of(
                                    ValidationError.builder()
                                            .field("kafka")
                                            .message(outbound.getMessage())
                                            .build()
                            )
                    );
                }
                return outbound.getResponseList().stream()
                        .map(this::convertToNoteResponseDto)
                        .toList();
            }

            throw new IllegalStateException("Unexpected response type: " + response.getClass());

        } catch (final NoteNotFoundException e) {
            throw e;
        } catch (final BusinessValidationException e) {
            throw e;
        } catch (final Exception e) {
            log.error("Failed to wait for Kafka response: {}", correlationId, e);
            throw new BusinessValidationException(
                    List.of(
                            ValidationError.builder()
                                    .field("kafka")
                                    .message("Failed to process request: " + e.getMessage())
                                    .build()
                    )
            );
        }
    }

    private void waitForVoidResponse(final String correlationId) {
        try {
            correlationManager.waitForResponse(
                    correlationId,
                    Object.class,
                    KAFKA_RESPONSE_TIMEOUT_SECONDS,
                    TimeUnit.SECONDS
            );
        } catch (final NoteNotFoundException e) {
            throw e;
        } catch (final BusinessValidationException e) {
            throw e;
        } catch (final Exception e) {
            log.error("Failed to wait for Kafka response: {}", correlationId, e);
            throw new BusinessValidationException(
                    List.of(
                            ValidationError.builder()
                                    .field("kafka")
                                    .message("Failed to process request: " + e.getMessage())
                                    .build()
                    )
            );
        }
    }

    private NoteResponseDto convertToNoteResponseDto(final NoteOutboundEvent.NoteResponseData data) {
        return NoteResponseDto.builder()
                .id(data.getId())
                .topicId(data.getTopicId())
                .country(data.getCountry())
                .content(data.getContent())
                .build();
    }

    private NoteResponseDto convertToNoteResponseDto(final NoteOutboundEvent outbound) {
        if (outbound.getResponseData() == null) {
            return null;
        }
        return convertToNoteResponseDto(outbound.getResponseData());
    }
}