using Distcomp.Shared.Models;
using System.Text.Json.Serialization;

namespace Distcomp.Application.DTOs
{
    public record UserRequestTo(
        long? Id,
        [property: JsonPropertyName("login")] string Login,
        [property: JsonPropertyName("password")] string Password,
        [property: JsonPropertyName("firstname")] string FirstName,
        [property: JsonPropertyName("lastname")] string LastName,
        [property: JsonPropertyName("role")] UserRole Role
    );
}