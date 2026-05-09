package by.bsuir.task320.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TweetRequestTo(
        Long id,
        @JsonProperty("userId")
        Long userId,
        @NotBlank(message = "Tweet title must not be blank")
        @Size(min = 2, max = 64, message = "Tweet title length must be between 2 and 64")
        String title,
        @NotBlank(message = "Tweet content must not be blank")
        @Size(min = 4, max = 2048, message = "Tweet content length must be between 4 and 2048")
        String content,
        String created,
        String modified,
        @JsonAlias("tagNames")
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        List<String> tags,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        List<Long> tagIds
) {
}
