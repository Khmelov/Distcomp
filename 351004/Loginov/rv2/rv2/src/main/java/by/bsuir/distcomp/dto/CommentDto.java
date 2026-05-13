package by.bsuir.distcomp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentDto(
        Long id,
        @NotNull Long issueId,
        @NotBlank @Size(min = 2, max = 2048) String content,
        CommentState state
) {
    public CommentDto(Long id, Long issueId, String content) {
        this(id, issueId, content, null);
    }
}
