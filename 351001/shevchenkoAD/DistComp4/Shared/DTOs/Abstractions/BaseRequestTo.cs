using System.Text.Json.Serialization;

namespace Shared.DTOs.Abstractions;

public abstract record BaseRequestTo
{
    [JsonPropertyName("id")]
    public long? Id { get; init; }

    protected BaseRequestTo() { }
    protected BaseRequestTo(long? id) => Id = id;
}   