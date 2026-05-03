package by.bsuir.task310.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class TopicRequestTo {
    private Long id;

    @NotNull
    private Long authorId;

    @NotBlank
    @Size(min = 2, max = 64)
    private String title;

    @NotBlank
    @Size(min = 4, max = 2048)
    private String content;

    private List<String> labels;
}