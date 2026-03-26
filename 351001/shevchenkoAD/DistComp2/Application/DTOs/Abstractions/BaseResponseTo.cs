using System.Text.Json.Serialization;

namespace Application.DTOs.Abstractions;

public abstract record BaseResponseTo(
    [property: JsonPropertyName("id")] long? Id
);