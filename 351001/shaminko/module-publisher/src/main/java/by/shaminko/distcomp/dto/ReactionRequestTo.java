package by.shaminko.distcomp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReactionRequestTo {
    long id;
    @Positive
    long articleId;
    @NotBlank
    @Size(min = 2, max = 2048)
    String content;
    String state;
}
