using System.Text.Json.Serialization;
using Shared.DTOs.Abstractions;

namespace Publisher.Application.DTOs.Responses;

public record AuthorResponseTo : BaseResponseTo
{
    [JsonPropertyName("login")] public string Login { get; init; } = null!;

    [JsonPropertyName("firstname")] public string Firstname { get; init; } = null!;

    [JsonPropertyName("lastname")] public string Lastname { get; init; } = null!;
}