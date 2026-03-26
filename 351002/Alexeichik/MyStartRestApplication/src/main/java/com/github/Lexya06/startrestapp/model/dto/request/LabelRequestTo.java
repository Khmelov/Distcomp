package com.github.Lexya06.startrestapp.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

import java.util.HashSet;
import java.util.Set;


@Value
@Builder
public class LabelRequestTo {
    @NotBlank
    @Size(min = 2, max = 32)
    String name;

    Set<Long> articleIds = new HashSet<>();
}
