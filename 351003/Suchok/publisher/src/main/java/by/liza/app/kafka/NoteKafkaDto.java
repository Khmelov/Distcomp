package by.liza.app.kafka;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class NoteKafkaDto {
    private Long id;
    private Long articleId;
    private String content;
    private String state; // PENDING | APPROVE | DECLINE
}