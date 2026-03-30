package by.tracker.rest_api.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

@Data
public class TweetResponseTo {
    private Long id;

    @JsonProperty("creatorId")  // может и не нужно, но для единообразия
    private Long creatorId;

    private String title;
    private String content;
    private LocalDateTime created;
    private LocalDateTime modified;
}