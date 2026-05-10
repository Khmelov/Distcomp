using System.Text.Json.Serialization;

namespace Distcomp.Application.DTOs
{
    public record UserResponseTo(
        long Id,
        string Login,
        [property: JsonPropertyName("firstname")] string FirstName,
        [property: JsonPropertyName("lastname")] string LastName
    );
}