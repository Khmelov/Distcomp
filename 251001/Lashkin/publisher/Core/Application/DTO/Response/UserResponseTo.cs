using System.Text.Json.Serialization;

namespace Application.DTO.Response;

public record UserResponseTo(
    long Id,
    string Login,
    string Password,
    [property: JsonPropertyName("firstname")]
    string FirstName,
    [property: JsonPropertyName("lastname")]
    string LastName);