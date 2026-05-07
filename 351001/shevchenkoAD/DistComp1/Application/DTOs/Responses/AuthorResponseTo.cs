using System.Text.Json.Serialization;
using Application.DTOs.Abstractions;

namespace Application.DTOs.Responses;

public record AuthorResponseTo(
    long Id,
    [property: JsonPropertyName("login")] string Login,
    [property: JsonPropertyName("firstname")]
    string Firstname,
    [property: JsonPropertyName("lastname")]
    string Lastname
)
    : BaseResponseTo(Id);