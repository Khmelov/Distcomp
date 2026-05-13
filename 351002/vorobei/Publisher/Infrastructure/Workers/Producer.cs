using System.Text.Json;
using Confluent.Kafka;
using Microsoft.Extensions.Configuration;

public enum MessageType { Create, Update, Get, GetAll, Delete }

public class KafkaMessageEnvelope
{
    public MessageType Type { get; set; }
    public string Payload { get; set; }
}

public class PostModerationProducer : IDisposable
{
    private readonly IProducer<string, string> _producer;
    private const string InTopic = "InTopic";

    public PostModerationProducer(IConfiguration config)
    {
        var producerConfig = new ProducerConfig
        {
            BootstrapServers = config["Kafka:BootstrapServers"],
            EnableIdempotence = true,
            Acks = Acks.All
        };
        _producer = new ProducerBuilder<string, string>(producerConfig).Build();
    }

    public async Task SendAsync(string key, MessageType type, object data)
    {
        var envelope = new KafkaMessageEnvelope
        {
            Type = type,
            Payload = JsonSerializer.Serialize(data)
        };

        var message = new Message<string, string>
        {
            Key = key,
            Value = JsonSerializer.Serialize(envelope)
        };

        await _producer.ProduceAsync(InTopic, message);
    }

    public void Dispose() => _producer?.Dispose();
}