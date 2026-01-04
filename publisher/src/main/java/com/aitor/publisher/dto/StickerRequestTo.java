package com.aitor.publisher.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StickerRequestTo {
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 32, message = "Name must be between 2 and 32 characters")
    private String name;
}