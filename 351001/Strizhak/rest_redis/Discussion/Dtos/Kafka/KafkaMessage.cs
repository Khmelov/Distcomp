using System.Text.Json;

namespace Discussion.Dtos.Kafka
{
    public class KafkaMessage
    {
        public string Method { get; set; }
        public string? CorrelationId { get; set; }
        public JsonElement Payload { get; set; }
    }
}
