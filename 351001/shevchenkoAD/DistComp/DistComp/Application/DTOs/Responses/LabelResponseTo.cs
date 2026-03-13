using System.Text.Json.Serialization;
using DistComp.Application.DTOs.Abstractions;

namespace DistComp.Application.DTOs.Responses;

public record LabelResponseTo(
    long Id,
    [property: JsonPropertyName("name")] string Name
)
    : BaseResponseTo(Id);