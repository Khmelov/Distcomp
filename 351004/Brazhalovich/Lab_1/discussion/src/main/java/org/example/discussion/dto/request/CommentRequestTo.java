package org.example.discussion.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestTo {
    @NotNull
    private Long newsId;

    @Size(min = 2, max = 2048)
    private String content;
}