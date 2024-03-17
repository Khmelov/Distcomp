using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;

namespace TaskREST.Dto.Request.UpdateTo;

public record UpdateCreatorRequestTo(
    [property: JsonPropertyName("id")] long Id,
    [property: JsonPropertyName("login")]
    [StringLength(64, MinimumLength = 2)]
    string Login,
    [property: JsonPropertyName("password")]
    [StringLength(128, MinimumLength = 8)]
    string Password,
    [property: JsonPropertyName("firstname")]
    [StringLength(64, MinimumLength = 2)]
    string FirstName,
    [property: JsonPropertyName("lastname")]
    [StringLength(64, MinimumLength = 2)]
    string LastName
);