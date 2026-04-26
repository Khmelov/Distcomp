package by.tracker.rest_api.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class NoteResponseTo {
    private Long id;

    @JsonProperty("tweetId")
    private Long tweetId;

    private String content;
}