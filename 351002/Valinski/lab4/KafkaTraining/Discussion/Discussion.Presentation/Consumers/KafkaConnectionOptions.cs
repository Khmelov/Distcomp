namespace Discussion.Presentation.Consumers;

public class KafkaConnectionOptions
{
    public string BootstrapServers { get; set; } = string.Empty;
    public string InTopic { get; set; } = string.Empty;
    public string OutTopic { get; set; } = string.Empty;
    public string GroupId { get; set; } = string.Empty;
}
