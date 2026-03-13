using System.Text.Json.Serialization;
using DistComp.Application.DTOs.Abstractions;

namespace DistComp.Application.DTOs.Responses;

public record AuthorResponseTo(
    long Id,
    [property: JsonPropertyName("login")] string Login,
    [property: JsonPropertyName("firstname")]
    string Firstname,
    [property: JsonPropertyName("lastname")]
    string Lastname
)
    : BaseResponseTo(Id);