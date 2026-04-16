package by.bsuir.task310.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StoryRequestTo(
        Long id,
        @JsonProperty("userId")
        Long editorId,
        String title,
        String content,
        @JsonIgnore
        List<Long> tagIds
) {
}
