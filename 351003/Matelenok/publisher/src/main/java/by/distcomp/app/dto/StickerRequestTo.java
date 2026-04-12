package by.distcomp.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StickerRequestTo(
        @NotBlank
        @Size(min = 2, max = 32)
        String name
) {
}
