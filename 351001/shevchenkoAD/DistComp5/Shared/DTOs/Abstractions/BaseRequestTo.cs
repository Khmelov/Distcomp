using System.Text.Json.Serialization;

namespace Shared.DTOs.Abstractions;

public abstract record BaseRequestTo
{
    protected BaseRequestTo()
    {
    }

    protected BaseRequestTo(long? id)
    {
        Id = id;
    }

    [JsonPropertyName("id")] public long? Id { get; init; }
}