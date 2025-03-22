package by.kopvzakone.distcomp.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReactionRequestTo {
    long id;
    long tweetId;
    @Size(min = 2, max = 32)
    String content;
}
