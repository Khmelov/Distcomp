package by.egorsosnovski.distcomp.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class TweetRequestTo {
    long id;
    long creatorId;
    @Size(min = 2, max = 64)
    String title;
    @Size(min = 4, max = 2048)
    String content;
    private List<String> stickers;
}
