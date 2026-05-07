using System.Text.Json.Serialization;

namespace Distcomp.Application.DTOs
{
    public record MarkerResponseTo(
    [property: JsonPropertyName("id")] long Id,
    [property: JsonPropertyName("name")] string Name
    );
}
