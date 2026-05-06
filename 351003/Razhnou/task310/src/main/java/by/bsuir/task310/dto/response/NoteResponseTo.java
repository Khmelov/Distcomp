package by.bsuir.task310.dto.response;

public record NoteResponseTo(
        Long id,
        @com.fasterxml.jackson.annotation.JsonProperty("tweetId")
        Long storyId,
        String content
) {
}
