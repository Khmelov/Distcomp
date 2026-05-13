namespace discussion.Kafka;

public sealed class KafkaOptions
{
    public const string SectionName = "Kafka";

    /// <summary>PLAINTEXT://localhost:9092 при запуске Confluent через Docker из задания.</summary>
    public string BootstrapServers { get; set; } = "localhost:9092";

    public string InTopic { get; set; } = "InTopic";
    public string OutTopic { get; set; } = "OutTopic";
    public string DiscussionConsumerGroup { get; set; } = "discussion-notice-consumer";
}
