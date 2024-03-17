using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;

namespace TaskREST.Dto.Request.CreateTo;

public record CreatePostRequestTo(
    [property: JsonPropertyName("tweetId")]
    long TweetId,
    [property: JsonPropertyName("content")]
    [StringLength(2048, MinimumLength = 2)]
    string Content
);