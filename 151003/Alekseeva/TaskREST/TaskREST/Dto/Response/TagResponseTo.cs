using System.Text.Json.Serialization;

namespace TaskREST.Dto.Response;

public record TagResponseTo(
    [property: JsonPropertyName("id")] long Id,
    [property: JsonPropertyName("name")] string Name
);