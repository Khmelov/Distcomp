package by.bsuir.distcomp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TagDto(
        Long id,
        Long issueId,
        @NotBlank @Size(min = 2, max = 32) String name
) {
}
