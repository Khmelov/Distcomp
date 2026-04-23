package com.distcomp.event.note;

import com.distcomp.event.abstraction.KafkaEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteOutboundEvent implements KafkaEvent, Serializable {

    private String eventId;
    private String eventType;
    private String correlationId;
    private OperationStatus status;
    private Instant timestamp;
    private String message;
    private NoteResponseData responseData;
    private List<NoteResponseData> responseList;
    private List<ValidationErrorData> errors;  

    public enum OperationStatus {
        SUCCESS, FAILURE, NOT_FOUND
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NoteResponseData implements Serializable {
        private Long id;
        private Long topicId;
        private String country;
        private String content;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationErrorData implements Serializable {
        private String field;
        private String message;
    }

    public static NoteOutboundEvent success(String correlationId, NoteResponseData data) {
        return NoteOutboundEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("NOTE_RESULT")
                .correlationId(correlationId)
                .status(OperationStatus.SUCCESS)
                .timestamp(Instant.now())
                .message("Operation completed successfully")
                .responseData(data)
                .build();
    }

    public static NoteOutboundEvent successList(String correlationId, List<NoteResponseData> dataList) {
        return NoteOutboundEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("NOTE_RESULT")
                .correlationId(correlationId)
                .status(OperationStatus.SUCCESS)
                .timestamp(Instant.now())
                .message("Operation completed successfully")
                .responseList(dataList)
                .build();
    }

    public static NoteOutboundEvent failure(String correlationId, String message) {
        return NoteOutboundEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("NOTE_RESULT")
                .correlationId(correlationId)
                .status(OperationStatus.FAILURE)
                .timestamp(Instant.now())
                .message(message)
                .build();
    }

    
    public static NoteOutboundEvent notFound(String correlationId, Long noteId, List<ValidationErrorData> errors) {
        return NoteOutboundEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("NOTE_RESULT")
                .correlationId(correlationId)
                .status(OperationStatus.NOT_FOUND)
                .timestamp(Instant.now())
                .message("Note not found: " + noteId)
                .errors(errors)
                .build();
    }

    
    public static NoteOutboundEvent notFound(String correlationId, Long noteId) {
        return notFound(correlationId, noteId, List.of(
                ValidationErrorData.builder()
                        .field("noteId")
                        .message("Note not found: " + noteId)
                        .build()
        ));
    }
}