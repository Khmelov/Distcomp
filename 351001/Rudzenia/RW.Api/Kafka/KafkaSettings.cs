namespace RW.Api.Kafka;

public class KafkaSettings
{
    public string BootstrapServers { get; set; } = "localhost:9092";
    public string InTopic { get; set; } = "InTopic";
    public string OutTopic { get; set; } = "OutTopic";
    public string GroupId { get; set; } = "publisher-group";
    public int RequestTimeoutMs { get; set; } = 10000;
}
