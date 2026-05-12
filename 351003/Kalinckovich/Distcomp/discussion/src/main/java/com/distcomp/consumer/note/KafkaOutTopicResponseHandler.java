package com.distcomp.consumer.note;

import com.distcomp.config.kafka.KafkaCorrelationManager;
import com.distcomp.config.kafka.KafkaTopic;
import com.distcomp.config.kafka.KafkaTopicProperties;
import com.distcomp.errorhandling.exceptions.BusinessValidationException;
import com.distcomp.errorhandling.exceptions.NoteNotFoundException;
import com.distcomp.errorhandling.model.ValidationError;
import com.distcomp.event.note.NoteOutboundEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaOutTopicResponseHandler {

    private final KafkaCorrelationManager correlationManager;
    private final KafkaTopicProperties kafkaTopicProperties;

    @KafkaListener(
            topics = "#{kafkaTopicProperties.getTopicName(T(com.distcomp.config.kafka.KafkaTopic).OUT_TOPIC)}",
            groupId = "${spring.kafka.consumer.group-id}-responses",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeOutTopic(@Payload NoteOutboundEvent event) {
        log.info("Received from OutTopic: eventId={}, correlationId={}, status={}",
                event.getEventId(),
                event.getCorrelationId(),
                event.getStatus());

        try {
            String correlationId = event.getCorrelationId();

            
            switch (event.getStatus()) {
                case SUCCESS -> {
                    log.debug("Successfully completed request: {}", correlationId);
                    correlationManager.completeRequest(correlationId, event);
                }
                case NOT_FOUND -> {
                    
                    log.warn("Note not found: correlationId={}, noteId={}",
                            correlationId,
                            event.getResponseData() != null ? event.getResponseData().getId() : "N/A");

                    NoteNotFoundException exception = buildNoteNotFoundException(event);
                    correlationManager.completeRequestWithError(correlationId, exception);
                }
                case FAILURE -> {
                    
                    log.error("Operation failed: correlationId={}, message={}",
                            correlationId, event.getMessage());

                    BusinessValidationException exception = new BusinessValidationException(
                            List.of(
                                    ValidationError.builder()
                                            .field("kafka")
                                            .message(event.getMessage())
                                            .build()
                            )
                    );
                    correlationManager.completeRequestWithError(correlationId, exception);
                }
                default -> {
                    log.error("Unknown status: {} for correlationId={}",
                            event.getStatus(), correlationId);

                    BusinessValidationException exception = new BusinessValidationException(
                            List.of(
                                    ValidationError.builder()
                                            .field("kafka")
                                            .message("Unknown status: " + event.getStatus())
                                            .build()
                            )
                    );
                    correlationManager.completeRequestWithError(correlationId, exception);
                }
            }

        } catch (Exception e) {
            log.error("Failed to process OutTopic response: {}", event.getCorrelationId(), e);
            correlationManager.completeRequestWithError(event.getCorrelationId(), e);
        }
    }

    /**
     * Build NoteNotFoundException from NoteOutboundEvent
     */
    private NoteNotFoundException buildNoteNotFoundException(NoteOutboundEvent event) {
        List<ValidationError> errors;

        if (event.getErrors() != null && !event.getErrors().isEmpty()) {
            errors = event.getErrors().stream()
                    .map(err -> ValidationError.builder()
                            .field(err.getField())
                            .message(err.getMessage())
                            .build())
                    .toList();
        } else {
            Long noteId = event.getResponseData() != null
                    ? event.getResponseData().getId()
                    : null;
            errors = List.of(
                    ValidationError.builder()
                            .field("noteId")
                            .message(noteId != null ? "Note not found: " + noteId : "Note not found")
                            .build()
            );
        }

        return new NoteNotFoundException(errors);
    }
}