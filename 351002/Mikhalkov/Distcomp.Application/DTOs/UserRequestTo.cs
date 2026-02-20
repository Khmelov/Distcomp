using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;

namespace Distcomp.Application.DTOs
{
    public record UserRequestTo(
        long? Id,
        string Login,
        string Password,
        [property: JsonPropertyName("firstname")] string FirstName,
        [property: JsonPropertyName("lastname")] string LastName
    );
}