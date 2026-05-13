package by.bsuir.publisher.dto.responses;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Data
@EqualsAndHashCode
public class CommentResponseDto {
    private Long id;
    private Long newsId;
    private String content;
}
