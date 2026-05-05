using System.Text.Json;
using Confluent.Kafka;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Options;
using RestApiTask.Models;
using RestApiTask.Models.Entities;
using RestApiTask.Repositories;

namespace RestApiTask.Services;

public class KafkaConsumerService : BackgroundService
{
    private readonly KafkaSettings _kafkaSettings;
    private readonly IServiceScopeFactory _scopeFactory;
    private readonly ILogger<KafkaConsumerService> _logger;

    public KafkaConsumerService(
        IOptions<KafkaSettings> kafkaOptions,
        IServiceScopeFactory scopeFactory,
        ILogger<KafkaConsumerService> logger)
    {
        _kafkaSettings = kafkaOptions.Value;
        _scopeFactory = scopeFactory;
        _logger = logger;
    }

    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        var config = new ConsumerConfig
        {
            BootstrapServers = _kafkaSettings.BootstrapServers,
            GroupId = _kafkaSettings.GroupId ?? "discussion-consumer",
            AutoOffsetReset = AutoOffsetReset.Earliest,
            EnableAutoCommit = true,
            AllowAutoCreateTopics = true
        };

        using var consumer = new ConsumerBuilder<long, string>(config)
            .SetErrorHandler((_, e) => _logger.LogError("Kafka consumer error: {Reason}", e.Reason))
            .Build();

        consumer.Subscribe(_kafkaSettings.Topic);
        _logger.LogInformation("Kafka consumer subscribed to topic {Topic}", _kafkaSettings.Topic);

        await Task.Yield();

        // Long-running poll loop for asynchronous consumption.
        while (!stoppingToken.IsCancellationRequested)
        {
            try
            {
                var result = consumer.Consume(stoppingToken);
                if (result?.Message?.Value is null)
                {
                    continue;
                }

                var message = JsonSerializer.Deserialize<KafkaMessage>(result.Message.Value);
                if (message is null)
                {
                    _logger.LogWarning("Kafka consumer received empty payload");
                    continue;
                }

                // Scope per message keeps repository lifetime safe for background processing.
                using var scope = _scopeFactory.CreateScope();
                var repo = scope.ServiceProvider.GetRequiredService<IRepository<Message>>();

                // Manual mapping to avoid AutoMapper requiring explicit maps
                // for the private nested KafkaMessage record type.
                var entity = new Message
                {
                    Id = message.Id,
                    ArticleId = message.ArticleId,
                    Content = message.Content,
                    CreatedAt = message.CreatedAt
                };
                if (entity.Id <= 0) entity.Id = DateTime.UtcNow.Ticks;
                if (entity.CreatedAt == default) entity.CreatedAt = DateTime.UtcNow;

                await repo.AddAsync(entity);
                _logger.LogInformation("Consumed Kafka message {MessageId} and saved to repository", entity.Id);
            }
            catch (ConsumeException ex)
            {
                _logger.LogError(ex, "Kafka consume error");
            }
            catch (OperationCanceledException)
            {
                break;
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Unexpected Kafka consumer error");
                await Task.Delay(TimeSpan.FromSeconds(5), stoppingToken);
            }
        }
    }

    private sealed record KafkaMessage(long Id, long ArticleId, string Content, DateTime CreatedAt);
}
