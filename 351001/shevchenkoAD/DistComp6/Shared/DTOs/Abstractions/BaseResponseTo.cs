using System.Text.Json.Serialization;

namespace Shared.DTOs.Abstractions;

public abstract record BaseResponseTo
{
    protected BaseResponseTo()
    {
    }

    protected BaseResponseTo(long? id)
    {
        Id = id;
    }

    [JsonPropertyName("id")] public long? Id { get; init; }
}