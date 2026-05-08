using System.Text.Json.Serialization;
using Shared.DTOs.Abstractions;

namespace Publisher.Application.DTOs.Responses;

public record LabelResponseTo : BaseResponseTo
{
    [JsonPropertyName("name")] public string Name { get; init; } = null!;
}