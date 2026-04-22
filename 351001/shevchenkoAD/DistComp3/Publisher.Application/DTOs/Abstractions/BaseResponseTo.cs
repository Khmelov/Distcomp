using System.Text.Json.Serialization;

namespace Publisher.Application.DTOs.Abstractions;

public abstract record BaseResponseTo
{
    [JsonPropertyName("id")]
    public long? Id { get; init; }

    protected BaseResponseTo() { }
    protected BaseResponseTo(long? id) => Id = id;
}