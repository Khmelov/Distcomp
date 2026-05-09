package by.boukhvalova.distcomp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NoteRequestTo {
    long id;
    @Positive
    long tweetId;
    long userId;
    @NotBlank
    @Size(min = 2, max = 2048)
    String content;
    String country;
}
