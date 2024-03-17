using System.Text.Json.Serialization;

namespace TaskREST.Dto.Response;

public record PostResponseTo(
    [property: JsonPropertyName("id")] long Id,
    [property: JsonPropertyName("tweetId")]
    long TweetId,
    [property: JsonPropertyName("content")]
    string Content
);