using System.Text.Json;

namespace discussion.Kafka;

public static class DiscussionKafkaJson
{
    public static readonly JsonSerializerOptions SerializerOptions = new()
    {
        PropertyNameCaseInsensitive = true,
        PropertyNamingPolicy = JsonNamingPolicy.CamelCase,
        WriteIndented = false
    };

    public static string SerializeNoticeOut(NoticeOutEnvelope env) =>
        JsonSerializer.Serialize(env, SerializerOptions);
}
