package by.bsuir.distcomp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record IssueDto(
        Long id,
        @NotNull Long writerId,
        @NotBlank @Size(min = 2, max = 64) String title,
        @NotBlank @Size(min = 4, max = 2048) String content,
        LocalDateTime created,
        LocalDateTime modified
) {
}
