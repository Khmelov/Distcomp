using Confluent.Kafka;
using System.Text.Json;

namespace RV_Kisel_lab2_Task320.Services;

public class KafkaProducerService
{
    private readonly IProducer<string, string> _producer;

    public KafkaProducerService(IConfiguration config)
    {
        var producerConfig = new ProducerConfig { BootstrapServers = config["Kafka:BootstrapServers"] ?? "localhost:9092" };
        _producer = new ProducerBuilder<string, string>(producerConfig).Build();
    }

    public async Task SendMessageAsync(string topic, string key, object message)
    {
        var value = JsonSerializer.Serialize(message);
        // Ключ key гарантирует, что сообщения одной Story (NewsId) попадут в одну партицию
        await _producer.ProduceAsync(topic, new Message<string, string> { Key = key, Value = value });
    }
}