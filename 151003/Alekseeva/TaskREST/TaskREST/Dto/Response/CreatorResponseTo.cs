using System.Text.Json.Serialization;

namespace TaskREST.Dto.Response;

public record CreatorResponseTo(
    [property: JsonPropertyName("id")] long Id,
    [property: JsonPropertyName("login")] string Login,
    [property: JsonPropertyName("password")]
    string Password,
    [property: JsonPropertyName("firstname")]
    string FirstName,
    [property: JsonPropertyName("lastname")]
    string LastName
);