using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;
using Shared.DTOs.Abstractions;

namespace Publisher.Application.DTOs.Requests;

public record LabelRequestTo : BaseRequestTo
{
    public LabelRequestTo() { }

    [Required]
    [StringLength(32, MinimumLength = 2)]
    [JsonPropertyName("name")]
    public string Name { get; init; } = null!;
}