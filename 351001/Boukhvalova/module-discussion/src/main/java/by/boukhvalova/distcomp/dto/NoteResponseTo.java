package by.boukhvalova.distcomp.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NoteResponseTo {
    long id;
    long tweetId;
    long userId;

    @Size(min = 2, max = 2048)
    String content;

    String country;
}
