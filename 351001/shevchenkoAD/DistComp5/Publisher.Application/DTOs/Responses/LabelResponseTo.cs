using System.Text.Json.Serialization;
using Shared.DTOs.Abstractions;

namespace Publisher.Application.DTOs.Responses;

public record LabelResponseTo : BaseResponseTo
{
    public LabelResponseTo() { }

    [JsonPropertyName("name")]
    public string Name { get; init; } = null!;
}