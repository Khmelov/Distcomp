using System.Text.Json.Serialization;

namespace RW.Api.Kafka;

public class KafkaRequestEnvelope
{
    [JsonPropertyName("method")]
    public string Method { get; set; } = string.Empty;

    [JsonPropertyName("payload")]
    public object? Payload { get; set; }
}

public class KafkaResponseEnvelope
{
    [JsonPropertyName("status")]
    public int Status { get; set; }

    [JsonPropertyName("data")]
    public object? Data { get; set; }

    [JsonPropertyName("error")]
    public string? Error { get; set; }
}

public static class KafkaMethods
{
    public const string GetAll = "GET_ALL";
    public const string GetById = "GET_BY_ID";
    public const string Create = "CREATE";
    public const string Update = "UPDATE";
    public const string Delete = "DELETE";
}
