using Confluent.Kafka;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.DependencyInjection;
using System.Text.Json;
using DiscussionService.Models.Dtos;
using DiscussionService.Models.Entities;
using DiscussionService.Repositories;

namespace DiscussionService.Services;

public class KafkaConsumerService : BackgroundService
{
    private readonly IServiceProvider _serviceProvider;

    // Внедряем IServiceProvider, чтобы доставать scoped-сервисы (репозиторий) внутри BackgroundService
    public KafkaConsumerService(IServiceProvider serviceProvider)
    {
        _serviceProvider = serviceProvider;
    }

    protected override Task ExecuteAsync(CancellationToken stoppingToken)
{
    return Task.Run(async () =>
    {
        var consumerConfig = new ConsumerConfig
        {
            BootstrapServers = "127.0.0.1:9092",
            // МЕНЯЕМ ИМЯ ГРУППЫ, чтобы пропустить старый мусор в топике
            GroupId = "discussion-group-v2", 
            AutoOffsetReset = AutoOffsetReset.Earliest
        };

        var producerConfig = new ProducerConfig
        {
            BootstrapServers = "127.0.0.1:9092"
        };

        using var consumer = new ConsumerBuilder<string, string>(consumerConfig).Build();
        using var producer = new ProducerBuilder<string, string>(producerConfig).Build();

        consumer.Subscribe("InTopic");
        Console.WriteLine("🔥 Discussion Kafka consumer started");

        while (!stoppingToken.IsCancellationRequested)
        {
            try
            {
                var cr = consumer.Consume(stoppingToken);
                
                try 
                {
                    // Пытаемся распарсить JSON
                    var msg = JsonSerializer.Deserialize<PostMessage>(cr.Message.Value, new JsonSerializerOptions { PropertyNameCaseInsensitive = true });

                    if (msg != null)
                    {
                        using var scope = _serviceProvider.CreateScope();
                        var repo = scope.ServiceProvider.GetRequiredService<IPostRepository>();

                        // Модерация
                        msg.State = msg.Content.Contains("bad", StringComparison.OrdinalIgnoreCase) ? "DECLINE" : "APPROVE";

                        // БД
                        if (msg.Action == "CREATE")
                        {
                            await repo.CreateAsync(new Post { Id = msg.Id, NewsId = msg.NewsId, Content = msg.Content, State = msg.State, Created = DateTime.UtcNow });
                        }
                        else if (msg.Action == "UPDATE")
                        {
                            await repo.UpdateAsync(msg.Id, new PostDto { Id = msg.Id, NewsId = msg.NewsId, Content = msg.Content, State = msg.State });
                        }
                        else if (msg.Action == "DELETE")
                        {
                            await repo.DeleteAsync(msg.Id);
                        }

                        // Отправка ответа
                        producer.Produce("OutTopic", new Message<string, string> { Key = cr.Message.Key, Value = JsonSerializer.Serialize(msg) });
                        Console.WriteLine($"✅ Processed {msg.Action} and forwarded to OutTopic with state {msg.State}");
                    }
                }
                catch (JsonException)
                {
                    // Если пришел не JSON (например, старое тестовое сообщение), просто игнорируем его
                    Console.WriteLine($"⚠️ Пропущено некорректное сообщение: {cr.Message.Value}");
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error: {ex.Message}");
            }
        }
    }, stoppingToken);
}
}