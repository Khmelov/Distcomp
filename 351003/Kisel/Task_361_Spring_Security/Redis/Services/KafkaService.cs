using Confluent.Kafka;
using System.Text.Json;
using Redis.Models;

namespace Redis.Services;

public class KafkaService
{
    private readonly IConfiguration _configuration;
    private readonly IProducer<Null, string> _producer;
    private readonly string _inTopic; // ИСПРАВЛЕНИЕ: Добавили переменную

    public KafkaService(IConfiguration configuration)
    {
        _configuration = configuration;
        var producerConfig = new ProducerConfig { BootstrapServers = _configuration["Kafka:BootstrapServers"] };
        _producer = new ProducerBuilder<Null, string>(producerConfig).Build();
        
        // ИСПРАВЛЕНИЕ: Читаем название топика из конфига
        _inTopic = _configuration["Kafka:InTopic"] ?? "InTopic"; 
    }

    public async Task SendPostRequestAsync(Post post, string operation)
    {
        // Оборачиваем в объект с полем Operation
        var messageObj = new { Operation = operation, Post = post };
        var message = JsonSerializer.Serialize(messageObj);
        
        await _producer.ProduceAsync(_inTopic, new Message<Null, string> { Value = message });
        Console.WriteLine($"Sent {operation} request for Post {post.Id} to Kafka");
    }

    public async Task<Post?> WaitForPostResponseAsync(int postId, TimeSpan timeout)
    {
        var consumerConfig = new ConsumerConfig
        {
            BootstrapServers = _configuration["Kafka:BootstrapServers"],
            GroupId = _configuration["Kafka:GroupId"] + Guid.NewGuid().ToString(),
            AutoOffsetReset = AutoOffsetReset.Latest
        };

        using var consumer = new ConsumerBuilder<Null, string>(consumerConfig).Build();
        consumer.Subscribe(_configuration["Kafka:OutTopic"]);

        var cts = new CancellationTokenSource(timeout);
        try
        {
            while (!cts.Token.IsCancellationRequested)
            {
                var consumeResult = consumer.Consume(cts.Token);
                if (consumeResult != null)
                {
                    var response = JsonSerializer.Deserialize<Post>(consumeResult.Message.Value, new JsonSerializerOptions { PropertyNameCaseInsensitive = true });
                    if (response != null && response.Id == postId)
                    {
                        return response;
                    }
                }
            }
        }
        catch (OperationCanceledException)
        {
            return null;
        }
        return null;
    }
}