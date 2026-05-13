using System.Text.Json;
using Confluent.Kafka;
using lab1.Kafka;
using Microsoft.Extensions.Options;

namespace lab1.Infrastructure;

/// <summary>Читает OutTopic и сопоставляет ответы с ожиданием RPC на publisher.</summary>
public sealed class PublisherOutTopicConsumerHostedService : BackgroundService
{
    private readonly NoticeRpcCorrelationRegistry _registry;
    private readonly KafkaTransportOptions _opt;
    private readonly ILogger<PublisherOutTopicConsumerHostedService> _logger;

    public PublisherOutTopicConsumerHostedService(
        NoticeRpcCorrelationRegistry registry,
        IOptions<KafkaTransportOptions> options,
        ILogger<PublisherOutTopicConsumerHostedService> logger)
    {
        _registry = registry;
        _opt = options.Value;
        _logger = logger;
    }

    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        await Task.Yield();

        var cfg = new ConsumerConfig
        {
            BootstrapServers = _opt.BootstrapServers,
            GroupId = _opt.PublisherOutConsumerGroup,
            AutoOffsetReset = AutoOffsetReset.Earliest,
            EnableAutoCommit = true,
            AllowAutoCreateTopics = true
        };

        using var consumer = new ConsumerBuilder<string, string>(cfg).Build();
        consumer.Subscribe(_opt.OutTopic);
        _logger.LogInformation("Publisher Kafka consumer subscribed to {Topic}", _opt.OutTopic);

        while (!stoppingToken.IsCancellationRequested)
        {
            ConsumeResult<string, string>? cr = null;
            try
            {
                cr = consumer.Consume(TimeSpan.FromSeconds(1));
            }
            catch (ConsumeException ex)
            {
                _logger.LogError(ex, "OutTopic consume error");
            }

            if (cr?.Message.Value == null)
                continue;

            try
            {
                var env = JsonSerializer.Deserialize<NoticeOutEnvelope>(cr.Message.Value, PublisherKafkaJson.Options);
                if (env?.CorrelationId is { Length: > 0 })
                    _registry.TryComplete(env);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Invalid OutTopic payload");
            }
        }

        consumer.Close();
    }
}
