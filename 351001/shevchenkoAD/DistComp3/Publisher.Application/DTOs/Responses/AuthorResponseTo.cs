using System.Text.Json.Serialization;
using Publisher.Application.DTOs.Abstractions;

namespace Publisher.Application.DTOs.Responses;

public record AuthorResponseTo : BaseResponseTo
{
    public AuthorResponseTo() { }

    [JsonPropertyName("login")]
    public string Login { get; init; } = null!;

    [JsonPropertyName("firstname")]
    public string Firstname { get; init; } = null!;

    [JsonPropertyName("lastname")]
    public string Lastname { get; init; } = null!;
}   