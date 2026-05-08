using System.Text.Json.Serialization;
using RW.Domain.Entities;

namespace RW.Application.DTOs.Response;

public class AuthorResponseTo
{
    [JsonPropertyName("id")]
    public long Id { get; set; }

    [JsonPropertyName("login")]
    public string Login { get; set; } = string.Empty;

    [JsonPropertyName("password")]
    public string Password { get; set; } = string.Empty;

    [JsonPropertyName("firstname")]
    public string FirstName { get; set; } = string.Empty;

    [JsonPropertyName("lastname")]
    public string LastName { get; set; } = string.Empty;

    [JsonPropertyName("role")]
    [JsonConverter(typeof(JsonStringEnumConverter))]
    public Role Role { get; set; } = Role.CUSTOMER;
}
