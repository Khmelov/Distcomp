package by.shaminko.distcomp.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReactionResponseTo {
    long id;
    long articleId;
    long creatorId;
    @Size(min = 2, max = 2048)
    String content;
    String state;
}
