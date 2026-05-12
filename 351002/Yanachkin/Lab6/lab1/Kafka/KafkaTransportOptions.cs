namespace lab1.Kafka;

public sealed class KafkaTransportOptions
{
    public const string SectionName = "Kafka";

    public string BootstrapServers { get; set; } = "localhost:9092";
    public string InTopic { get; set; } = "InTopic";
    public string OutTopic { get; set; } = "OutTopic";
    public string PublisherOutConsumerGroup { get; set; } = "publisher-out-consumer";
    public int RpcTimeoutMs { get; set; } = 1000;
}
