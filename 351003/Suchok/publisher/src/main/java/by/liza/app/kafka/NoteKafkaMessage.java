package by.liza.app.kafka;

import lombok.*;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class NoteKafkaMessage {
    private String requestId;
    private OperationType operation;
    private NoteKafkaDto note;
    private List<NoteKafkaDto> noteList;
    private Long noteId;
    private Long articleId;
    private Integer errorCode;
    private String errorMessage;

    public enum OperationType { CREATE, GET_BY_ID, GET_ALL, UPDATE, DELETE }
}