package by.bsuir.discussion.dto.requests;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class CommentRequestDto extends BaseRequestDto {
    private Long newsId;

    @Size(min = 3, max = 32)
    private String content;
}
