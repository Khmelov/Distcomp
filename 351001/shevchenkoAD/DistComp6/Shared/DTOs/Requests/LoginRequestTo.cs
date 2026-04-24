using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;

namespace Shared.DTOs.Requests;

public record LoginRequestTo
{
    [Required]
    [StringLength(64, MinimumLength = 2)]
    [JsonPropertyName("login")]
    public string Login { get; init; } = null!;

    [Required]
    [StringLength(128, MinimumLength = 8)]
    [JsonPropertyName("password")]
    public string Password { get; init; } = null!;
}