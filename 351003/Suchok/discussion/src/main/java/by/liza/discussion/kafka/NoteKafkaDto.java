package by.liza.discussion.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoteKafkaDto {
    private Long id;
    private Long articleId;
    private String content;
    private String state;
}