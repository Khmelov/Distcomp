using System.Text.Json;
using BlogService.Infrastructure.PostgreSQL.Context;
using Confluent.Kafka;
using Microsoft.Extensions.DependencyInjection;
using Shared.Domain.DTOs;
using Microsoft.Extensions.Hosting;

namespace BlogService.Infrastructure.Kafka;

public class PublisherResponseWorker : BackgroundService
{
    private readonly IServiceProvider _serviceProvider;
    public PublisherResponseWorker(IServiceProvider serviceProvider) => _serviceProvider = serviceProvider;

    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        var config = new ConsumerConfig
        {
            BootstrapServers = "127.0.0.1:9092", 
            GroupId = $"blog-group-{Guid.NewGuid()}", // Уникальный ID для тестов
            AutoOffsetReset = AutoOffsetReset.Earliest,
            FetchWaitMaxMs = 5,   // Опрос каждые 5мс
            FetchMinBytes = 1     // Читать сразу
        };

        using var consumer = new ConsumerBuilder<string, string>(config).Build();
        consumer.Subscribe("OutTopic");

        var options = new JsonSerializerOptions { PropertyNameCaseInsensitive = true };

        while (!stoppingToken.IsCancellationRequested)
        {
            try
            {
                // Используем таймаут вместо блокирующего Consume(stoppingToken)
                var result = consumer.Consume(TimeSpan.FromMilliseconds(100));
                if (result == null) continue;

                var msg = JsonSerializer.Deserialize<CommentKafkaMessage>(result.Message.Value, options);
                if (msg == null) continue;

                using var scope = _serviceProvider.CreateScope();
                var db = scope.ServiceProvider.GetRequiredService<BlogServiceDbContext>();
                
                var comment = await db.Comment.FindAsync(msg.ID);
                if (comment != null)
                {
                    comment.State = msg.State; 
                    await db.SaveChangesAsync();
                    Console.WriteLine($"[BlogService] Статус комментария {msg.ID} обновлен на {msg.State}");
                }
            }
            catch (ConsumeException e) when (e.Error.Code == ErrorCode.UnknownTopicOrPart)
            {
                await Task.Delay(200, stoppingToken); // Уменьшили с 5000 до 200мс
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[BlogService Worker Error]: {ex.Message}");
                await Task.Delay(100, stoppingToken); // Уменьшили с 1000 до 100мс
            }
        }
    }
}