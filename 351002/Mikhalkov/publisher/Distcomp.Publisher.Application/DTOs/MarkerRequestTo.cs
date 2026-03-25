using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;

namespace Distcomp.Application.DTOs
{
    public record MarkerRequestTo(
    [property: JsonPropertyName("id")] long? Id,
    [property: JsonPropertyName("name")] string Name
    );
}
