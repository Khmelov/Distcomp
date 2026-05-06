package by.bsuir.task310.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NoteRequestTo(
        Long id,
        @JsonProperty("tweetId")
        Long storyId,
        String content
) {
}
