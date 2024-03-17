using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;

namespace TaskREST.Dto.Request.CreateTo;

public record CreateTweetRequestTo(
    [property: JsonPropertyName("creatorId")]
    long CreatorId,
    [property: JsonPropertyName("title")]
    [StringLength(64, MinimumLength = 2)]
    string Title,
    [property: JsonPropertyName("content")]
    [StringLength(2048, MinimumLength = 4)]
    string Content
);