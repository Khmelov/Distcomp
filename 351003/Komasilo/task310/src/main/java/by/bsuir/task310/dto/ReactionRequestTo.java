package by.bsuir.task310.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReactionRequestTo {
    private Long id;

    @NotNull
    private Long topicId;

    @NotBlank
    @Size(min = 2, max = 2048)
    private String content;
}