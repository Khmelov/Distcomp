package by.bsuir.task310.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record StoryResponseTo(
        Long id,
        @JsonProperty("userId")
        Long editorId,
        String title,
        String content,
        String created,
        String modified,
        @JsonIgnore
        List<Long> tagIds
) {
}
