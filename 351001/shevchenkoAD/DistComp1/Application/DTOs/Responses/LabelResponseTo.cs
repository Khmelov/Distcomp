using System.Text.Json.Serialization;
using Application.DTOs.Abstractions;

namespace Application.DTOs.Responses;

public record LabelResponseTo(
    long Id,
    [property: JsonPropertyName("name")] string Name
)
    : BaseResponseTo(Id);