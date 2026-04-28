namespace Publisher.Presentation.Options;

public class KafkaReactionConnectionOptions
{
    public string BootstrapServers { get; set; } = string.Empty;
    public string InTopic { get; set; } = string.Empty;
    public string OutTopic { get; set; } = string.Empty;
    public string GroupId { get; set; } = string.Empty;
}
