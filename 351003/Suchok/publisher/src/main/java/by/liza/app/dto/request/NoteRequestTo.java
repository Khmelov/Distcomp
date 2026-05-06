package by.liza.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NoteRequestTo {

    private Long id;

    @NotNull(message = "ArticleId cannot be null")
    private Long articleId;

    @NotBlank(message = "Content cannot be blank")
    @Size(min = 2, message = "Content must be at least 2 characters")
    private String content;
}