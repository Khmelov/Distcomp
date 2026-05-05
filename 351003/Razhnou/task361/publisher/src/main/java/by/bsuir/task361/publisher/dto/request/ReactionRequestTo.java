package by.bsuir.task361.publisher.dto.request;

import by.bsuir.task361.publisher.dto.ReactionState;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ReactionRequestTo(
        Long id,
        @JsonProperty("tweetId")
        Long tweetId,
        @NotBlank(message = "Reaction content must not be blank")
        @Size(min = 2, max = 2048, message = "Reaction content length must be between 2 and 2048")
        String content,
        ReactionState state
) {
}
