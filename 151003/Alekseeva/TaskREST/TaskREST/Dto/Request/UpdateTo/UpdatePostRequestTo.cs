using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;

namespace TaskREST.Dto.Request.UpdateTo;

public record UpdatePostRequestTo(
    [property: JsonPropertyName("id")] long Id,
    [property: JsonPropertyName("tweetId")]
    long TweetId,
    [property: JsonPropertyName("content")]
    [StringLength(2048, MinimumLength = 2)]
    string Content
);