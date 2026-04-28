package com.distcomp.consumer.note;

import com.distcomp.config.kafka.KafkaTopic;
import com.distcomp.config.kafka.KafkaTopicProperties;
import com.distcomp.errorhandling.exceptions.NoteNotFoundException;
import com.distcomp.errorhandling.model.ValidationError;
import com.distcomp.event.note.NoteInboundEvent;
import com.distcomp.event.note.NoteOutboundEvent;
import com.distcomp.mapper.note.NoteMapper;
import com.distcomp.model.note.Note;
import com.distcomp.publisher.abstraction.KafkaEventPublisher;
import com.distcomp.repository.cassandra.NoteCassandraReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NoteInTopicConsumerHandler {

    private final NoteCassandraReactiveRepository noteRepository;
    private final KafkaEventPublisher kafkaEventPublisher;

    private static final String DEFAULT_COUNTRY = "default";

    @KafkaListener(
            topics = "#{kafkaTopicProperties.getTopicName(T(com.distcomp.config.kafka.KafkaTopic).IN_TOPIC)}",
            groupId = "${spring.kafka.consumer.group-id}-processor",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeInTopic(@Payload NoteInboundEvent event, Acknowledgment ack) {
        log.info("Received from InTopic: eventId={}, operation={}, topicId={}, noteId={}",
                event.getEventId(),
                event.getOperationType(),
                event.getNoteData() != null ? event.getNoteData().getTopicId() : "N/A",
                event.getNoteData() != null ? event.getNoteData().getId() : "N/A");

        try {
            switch (event.getOperationType()) {
                case CREATE -> handleCreate(event);
                case UPDATE -> handleUpdate(event);
                case PATCH -> handlePatch(event);
                case DELETE -> handleDelete(event);
                case DELETE_BY_TOPIC_ID -> handleDeleteByTopicId(event);
                case FIND_BY_ID -> handleFindById(event);  
                case FIND_ALL -> handleFindAll(event);
            }
            ack.acknowledge();
            log.info("Successfully processed event: {}", event.getEventId());

        } catch (NoteNotFoundException e) {
            
            log.warn("Note not found: eventId={}, noteId={}", event.getEventId(),
                    event.getNoteData() != null ? event.getNoteData().getId() : "N/A");
            sendNotFoundResponse(event.getEventId(),
                    event.getNoteData() != null ? event.getNoteData().getId() : null);
            ack.acknowledge();

        } catch (Exception e) {
            log.error("Failed to process event: {}", event.getEventId(), e);
            sendFailureResponse(event.getEventId(), e.getMessage());
            ack.acknowledge();
        }
    }

    private void handleCreate(NoteInboundEvent event) {
        NoteInboundEvent.NoteData data = event.getNoteData();

        Note note = new Note();
        note.setKey(new Note.NoteKey(
                data.getCountry() != null ? data.getCountry() : DEFAULT_COUNTRY,
                data.getTopicId(),
                data.getId()
        ));
        note.setContent(data.getContent());

        noteRepository.save(note).block();

        NoteOutboundEvent.NoteResponseData responseData = NoteOutboundEvent.NoteResponseData.builder()
                .id(data.getId())
                .topicId(data.getTopicId())
                .country(data.getCountry())
                .content(data.getContent())
                .build();

        sendSuccessResponse(event.getEventId(), responseData);
    }

    private void handleUpdate(NoteInboundEvent event) {
        NoteInboundEvent.NoteData data = event.getNoteData();

        Note existing;
        if (data.getTopicId() != null) {
            existing = noteRepository.findById(new Note.NoteKey(DEFAULT_COUNTRY, data.getTopicId(), data.getId())).block();
        } else {
            existing = noteRepository.findByNoteId(data.getId()).block();
        }

        
        if (existing == null) {
            throw new NoteNotFoundException(List.of(
                    new ValidationError("noteId", "Note not found: " + data.getId())
            ));
        }

        existing.setContent(data.getContent());
        noteRepository.save(existing).block();

        NoteOutboundEvent.NoteResponseData responseData = NoteOutboundEvent.NoteResponseData.builder()
                .id(data.getId())
                .topicId(existing.getTopicId())
                .country(existing.getCountry())
                .content(data.getContent())
                .build();

        sendSuccessResponse(event.getEventId(), responseData);
    }

    private void handlePatch(NoteInboundEvent event) {
        NoteInboundEvent.NoteData data = event.getNoteData();

        Note existing;
        if (data.getTopicId() != null) {
            existing = noteRepository.findById(new Note.NoteKey(DEFAULT_COUNTRY, data.getTopicId(), data.getId())).block();
        } else {
            existing = noteRepository.findByNoteId(data.getId()).block();
        }

        
        if (existing == null) {
            throw new NoteNotFoundException(List.of(
                    new ValidationError("noteId", "Note not found: " + data.getId())
            ));
        }

        if (data.getContent() != null) {
            existing.setContent(data.getContent());
            noteRepository.save(existing).block();
        }

        NoteOutboundEvent.NoteResponseData responseData = NoteOutboundEvent.NoteResponseData.builder()
                .id(data.getId())
                .topicId(existing.getTopicId())
                .country(existing.getCountry())
                .content(existing.getContent())
                .build();

        sendSuccessResponse(event.getEventId(), responseData);
    }

    private void handleDelete(NoteInboundEvent event) {
        NoteInboundEvent.NoteData data = event.getNoteData();

        if (data.getTopicId() != null) {
            Note.NoteKey key = new Note.NoteKey(DEFAULT_COUNTRY, data.getTopicId(), data.getId());
            noteRepository.deleteById(key).block();
        } else {
            
            Note existing = noteRepository.findByNoteId(data.getId()).block();
            if (existing != null) {
                noteRepository.deleteById(existing.getKey()).block();
            }
            
        }

        sendSuccessResponse(event.getEventId(), null);
    }

    private void handleDeleteByTopicId(NoteInboundEvent event) {
        NoteInboundEvent.NoteData data = event.getNoteData();

        noteRepository.deleteByCountryAndTopicId(DEFAULT_COUNTRY, data.getTopicId()).block();

        sendSuccessResponse(event.getEventId(), null);
    }

    private void handleFindById(NoteInboundEvent event) {
        NoteInboundEvent.NoteData data = event.getNoteData();

        
        Note note;
        if (data.getTopicId() != null) {
            note = noteRepository.findById(new Note.NoteKey(DEFAULT_COUNTRY, data.getTopicId(), data.getId())).block();
        } else {
            note = noteRepository.findByNoteId(data.getId()).block();
        }

        
        if (note == null) {
            throw new NoteNotFoundException(List.of(
                    new ValidationError("noteId", "Note not found: " + data.getId())
            ));
        }

        NoteOutboundEvent.NoteResponseData responseData = NoteOutboundEvent.NoteResponseData.builder()
                .id(note.getId())
                .topicId(note.getTopicId())
                .country(note.getCountry())
                .content(note.getContent())
                .build();

        sendSuccessResponse(event.getEventId(), responseData);
    }

    private void handleFindAll(NoteInboundEvent event) {
        NoteInboundEvent.NoteData data = event.getNoteData();
        int page = data.getPage() != null ? data.getPage() : 0;
        int size = data.getSize() != null ? data.getSize() : 10;

        List<Note> notes;
        if (data.getTopicId() != null) {
            notes = noteRepository.findByKeyCountryAndKeyTopicId(
                    DEFAULT_COUNTRY, data.getTopicId(), PageRequest.of(page, size)
            ).collectList().block();
        } else {
            notes = noteRepository.findByKeyCountry(
                    DEFAULT_COUNTRY, PageRequest.of(page, size)
            ).collectList().block();
        }

        List<NoteOutboundEvent.NoteResponseData> responseDataList = notes.stream()
                .map(note -> NoteOutboundEvent.NoteResponseData.builder()
                        .id(note.getId())
                        .topicId(note.getTopicId())
                        .country(note.getCountry())
                        .content(note.getContent())
                        .build())
                .toList();

        sendSuccessListResponse(event.getEventId(), responseDataList);
    }

    private void sendSuccessResponse(String correlationId, NoteOutboundEvent.NoteResponseData data) {
        NoteOutboundEvent response = data != null
                ? NoteOutboundEvent.success(correlationId, data)
                : NoteOutboundEvent.builder()
                .eventId(java.util.UUID.randomUUID().toString())
                .eventType("NOTE_RESULT")
                .correlationId(correlationId)
                .status(NoteOutboundEvent.OperationStatus.SUCCESS)
                .timestamp(java.time.Instant.now())
                .message("Operation completed successfully")
                .build();

        kafkaEventPublisher.publish(KafkaTopic.OUT_TOPIC, correlationId, response);
    }

    private void sendSuccessListResponse(String correlationId, List<NoteOutboundEvent.NoteResponseData> dataList) {
        NoteOutboundEvent response = NoteOutboundEvent.successList(correlationId, dataList);
        kafkaEventPublisher.publish(KafkaTopic.OUT_TOPIC, correlationId, response);
    }

    private void sendFailureResponse(String correlationId, String message) {
        NoteOutboundEvent response = NoteOutboundEvent.failure(correlationId, message);
        kafkaEventPublisher.publish(KafkaTopic.OUT_TOPIC, correlationId, response);
    }

    private void sendNotFoundResponse(String correlationId, Long noteId) {
        NoteOutboundEvent response = NoteOutboundEvent.notFound(correlationId, noteId);
        kafkaEventPublisher.publish(KafkaTopic.OUT_TOPIC, correlationId, response);
    }
}