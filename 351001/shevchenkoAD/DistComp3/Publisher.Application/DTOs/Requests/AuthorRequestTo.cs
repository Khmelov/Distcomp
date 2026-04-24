using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;
using Publisher.Application.DTOs.Abstractions;

namespace Publisher.Application.DTOs.Requests;

public record AuthorRequestTo : BaseRequestTo
{
    public AuthorRequestTo() { }

    [Required]
    [StringLength(64, MinimumLength = 2)]
    [JsonPropertyName("login")]
    public string Login { get; init; } = null!;

    [Required]
    [StringLength(128, MinimumLength = 8)]
    [JsonPropertyName("password")]
    public string Password { get; init; } = null!;

    [Required]
    [StringLength(64, MinimumLength = 2)]
    [JsonPropertyName("firstname")]
    public string Firstname { get; init; } = null!;

    [Required]
    [StringLength(64, MinimumLength = 2)]
    [JsonPropertyName("lastname")]
    public string Lastname { get; init; } = null!;
}