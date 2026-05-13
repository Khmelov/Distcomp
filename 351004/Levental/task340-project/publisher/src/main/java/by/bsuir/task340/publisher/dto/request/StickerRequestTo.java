package by.bsuir.task340.publisher.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StickerRequestTo(
        Long id,
        @NotBlank @Size(min = 2, max = 32) String name
) {}
