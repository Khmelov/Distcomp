package com.github.Lexya06.startrestapp.publisher.api.dto.label;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;


@Value
@Builder
public class LabelRequestTo {
    @NotBlank
    @Size(min = 2, max = 32)
    String name;
}
