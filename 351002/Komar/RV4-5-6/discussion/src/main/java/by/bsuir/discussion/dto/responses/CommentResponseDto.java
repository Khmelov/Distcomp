package by.bsuir.discussion.dto.responses;

import by.bsuir.discussion.dto.CommentState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class CommentResponseDto extends BaseResponseDto {
    private Long newsId;
    private String content;
    private CommentState state;
}
