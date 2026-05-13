using Confluent.Kafka;
using System.Text.Json;
using Shared.Domain.DTOs;
using Shared.Domain.Interfaces;

namespace BlogService.Infrastructure.Kafka;

public class KafkaProducer : ICommentMessageProducer
{
    private readonly string _topic = "InTopic";
    private readonly IProducer<string, string> _producer;

    public KafkaProducer()
    {
        var config = new ProducerConfig 
        { 
            BootstrapServers = "127.0.0.1:9092", // IP быстрее чем localhost
            LingerMs = 0,                         // НЕ ждать накопления пачки сообщений
            Acks = Acks.Leader,                   // Ждать подтверждения только от лидера
            SocketNagleDisable = true             // Отключить алгоритм Нагла
        };
        _producer = new ProducerBuilder<string, string>(config).Build();
    }

    public async Task SendCommentAsync(CommentKafkaMessage message)
    {
        var json = JsonSerializer.Serialize(message);
        var kafkaMessage = new Message<string, string> 
        { 
            Key = message.StoryID.ToString(),
            Value = json 
        };
        
        await _producer.ProduceAsync(_topic, kafkaMessage);
        // ПРИНУДИТЕЛЬНО выталкиваем сообщение из памяти в сеть прямо сейчас
        _producer.Flush(TimeSpan.FromSeconds(1)); 
    }
}