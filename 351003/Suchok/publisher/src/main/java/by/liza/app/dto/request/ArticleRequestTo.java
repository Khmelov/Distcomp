package by.liza.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ArticleRequestTo {

    private Long id;

    @NotNull(message = "WriterId cannot be null")
    private Long writerId;

    @NotBlank(message = "Title cannot be blank")
    @Size(min = 2, max = 64, message = "Title must be between 2 and 64 characters")
    private String title;

    @NotBlank(message = "Content cannot be blank")
    @Size(min = 4, message = "Content must be at least 4 characters")
    private String content;

    private List<Long> markIds = new ArrayList<>();

    private List<String> marks = new ArrayList<>();
}