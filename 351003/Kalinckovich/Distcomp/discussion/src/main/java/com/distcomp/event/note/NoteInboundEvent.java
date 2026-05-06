package com.distcomp.event.note;

import com.distcomp.event.abstraction.KafkaEvent;
import com.distcomp.dto.note.NoteCreateRequest;
import com.distcomp.dto.note.NotePatchRequest;
import com.distcomp.dto.note.NoteUpdateRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteInboundEvent implements KafkaEvent, Serializable {

    private String eventId;
    private String eventType;
    private Instant timestamp;
    private OperationType operationType;
    private NoteData noteData;

    public enum OperationType {
        CREATE, UPDATE, PATCH, DELETE, FIND_BY_ID, FIND_ALL, DELETE_BY_TOPIC_ID
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NoteData implements Serializable {
        private Long id;
        private Long topicId;
        private String country;
        private String content;
        private Integer page;
        private Integer size;
    }

    // CREATE
    public static NoteInboundEvent create(String correlationId, NoteCreateRequest request, Long generatedId, String country) {
        return NoteInboundEvent.builder()
                .eventId(correlationId)
                .eventType("NOTE_CREATE")
                .timestamp(Instant.now())
                .operationType(OperationType.CREATE)
                .noteData(NoteData.builder()
                        .id(generatedId)
                        .topicId(request.getTopicId())
                        .country(country)
                        .content(request.getContent())
                        .build())
                .build();
    }

    // UPDATE (topicId + noteId)
    public static NoteInboundEvent update(String correlationId, Long topicId, Long noteId, NoteUpdateRequest request, String country) {
        return NoteInboundEvent.builder()
                .eventId(correlationId)
                .eventType("NOTE_UPDATE")
                .timestamp(Instant.now())
                .operationType(OperationType.UPDATE)
                .noteData(NoteData.builder()
                        .id(noteId)
                        .topicId(topicId)
                        .country(country)
                        .content(request.getContent())
                        .build())
                .build();
    }

    // UPDATE (noteId only)
    public static NoteInboundEvent update(String correlationId, Long noteId, NoteUpdateRequest request) {
        return NoteInboundEvent.builder()
                .eventId(correlationId)
                .eventType("NOTE_UPDATE")
                .timestamp(Instant.now())
                .operationType(OperationType.UPDATE)
                .noteData(NoteData.builder()
                        .id(noteId)
                        .topicId(request.getTopicId())
                        .content(request.getContent())
                        .build())
                .build();
    }

    // PATCH (topicId + noteId)
    public static NoteInboundEvent patch(String correlationId, Long topicId, Long noteId, NotePatchRequest request, String country) {
        return NoteInboundEvent.builder()
                .eventId(correlationId)
                .eventType("NOTE_PATCH")
                .timestamp(Instant.now())
                .operationType(OperationType.PATCH)
                .noteData(NoteData.builder()
                        .id(noteId)
                        .topicId(topicId)
                        .country(country)
                        .content(request.getContent())
                        .build())
                .build();
    }

    // PATCH (noteId only)
    public static NoteInboundEvent patch(String correlationId, Long noteId, NotePatchRequest request) {
        return NoteInboundEvent.builder()
                .eventId(correlationId)
                .eventType("NOTE_PATCH")
                .timestamp(Instant.now())
                .operationType(OperationType.PATCH)
                .noteData(NoteData.builder()
                        .id(noteId)
                        .content(request.getContent())
                        .build())
                .build();
    }

    // DELETE (topicId + noteId)
    public static NoteInboundEvent delete(String correlationId, Long topicId, Long noteId) {
        return NoteInboundEvent.builder()
                .eventId(correlationId)
                .eventType("NOTE_DELETE")
                .timestamp(Instant.now())
                .operationType(OperationType.DELETE)
                .noteData(NoteData.builder()
                        .id(noteId)
                        .topicId(topicId)
                        .build())
                .build();
    }


    public static NoteInboundEvent delete(String correlationId, Long noteId) {
        return NoteInboundEvent.builder()
                .eventId(correlationId)
                .eventType("NOTE_DELETE")
                .timestamp(Instant.now())
                .operationType(OperationType.DELETE)
                .noteData(NoteData.builder()
                        .id(noteId)
                        .topicId(null)  // Explicitly null for legacy API
                        .build())
                .build();
    }

    // DELETE BY TOPIC ID
    public static NoteInboundEvent deleteByTopicId(String correlationId, Long topicId) {
        return NoteInboundEvent.builder()
                .eventId(correlationId)
                .eventType("NOTE_DELETE_BY_TOPIC_ID")
                .timestamp(Instant.now())
                .operationType(OperationType.DELETE_BY_TOPIC_ID)
                .noteData(NoteData.builder()
                        .topicId(topicId)
                        .build())
                .build();
    }

    // FIND BY ID (topicId + noteId)
    public static NoteInboundEvent findById(String correlationId, Long topicId, Long noteId) {
        return NoteInboundEvent.builder()
                .eventId(correlationId)
                .eventType("NOTE_FIND_BY_ID")
                .timestamp(Instant.now())
                .operationType(OperationType.FIND_BY_ID)
                .noteData(NoteData.builder()
                        .id(noteId)
                        .topicId(topicId)
                        .build())
                .build();
    }

    // FIND BY ID (noteId only)
    public static NoteInboundEvent findById(String correlationId, Long noteId) {
        return NoteInboundEvent.builder()
                .eventId(correlationId)
                .eventType("NOTE_FIND_BY_ID")
                .timestamp(Instant.now())
                .operationType(OperationType.FIND_BY_ID)
                .noteData(NoteData.builder()
                        .id(noteId)
                        .build())
                .build();
    }

    // FIND ALL (topicId + page + size)
    public static NoteInboundEvent findAll(String correlationId, Long topicId, int page, int size) {
        return NoteInboundEvent.builder()
                .eventId(correlationId)
                .eventType("NOTE_FIND_ALL")
                .timestamp(Instant.now())
                .operationType(OperationType.FIND_ALL)
                .noteData(NoteData.builder()
                        .topicId(topicId)
                        .page(page)
                        .size(size)
                        .build())
                .build();
    }

    // FIND ALL (page + size)
    public static NoteInboundEvent findAll(String correlationId, int page, int size) {
        return NoteInboundEvent.builder()
                .eventId(correlationId)
                .eventType("NOTE_FIND_ALL")
                .timestamp(Instant.now())
                .operationType(OperationType.FIND_ALL)
                .noteData(NoteData.builder()
                        .page(page)
                        .size(size)
                        .build())
                .build();
    }
}