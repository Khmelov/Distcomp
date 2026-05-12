using System.Text;
using Confluent.Kafka;
using Microsoft.Extensions.Options;

namespace RW.Api.Kafka;

public class KafkaResponseConsumer : BackgroundService
{
    private readonly IKafkaRequestClient _requestClient;
    private readonly KafkaSettings _settings;
    private readonly ILogger<KafkaResponseConsumer> _logger;

    public KafkaResponseConsumer(
        IKafkaRequestClient requestClient,
        IOptions<KafkaSettings> settings,
        ILogger<KafkaResponseConsumer> logger)
    {
        _requestClient = requestClient;
        _settings = settings.Value;
        _logger = logger;
    }

    protected override Task ExecuteAsync(CancellationToken stoppingToken)
    {
        return Task.Run(() => ConsumeLoop(stoppingToken), stoppingToken);
    }

    private void ConsumeLoop(CancellationToken stoppingToken)
    {
        var config = new ConsumerConfig
        {
            BootstrapServers = _settings.BootstrapServers,
            GroupId = _settings.GroupId + "-replies-" + Guid.NewGuid(),
            AutoOffsetReset = AutoOffsetReset.Latest,
            EnableAutoCommit = true
        };

        using var consumer = new ConsumerBuilder<string, string>(config).Build();
        consumer.Subscribe(_settings.OutTopic);
        _logger.LogInformation("Publisher consumer subscribed to {Topic}", _settings.OutTopic);

        try
        {
            while (!stoppingToken.IsCancellationRequested)
            {
                try
                {
                    var result = consumer.Consume(stoppingToken);
                    if (result?.Message is null) continue;

                    var correlationHeader = result.Message.Headers
                        .FirstOrDefault(h => h.Key == "correlationId");
                    if (correlationHeader is null) continue;

                    var correlationId = Encoding.UTF8.GetString(correlationHeader.GetValueBytes());
                    _requestClient.HandleResponse(correlationId, result.Message.Value);
                }
                catch (OperationCanceledException) { break; }
                catch (ConsumeException ex)
                {
                    _logger.LogError(ex, "Error while consuming OutTopic");
                }
            }
        }
        finally
        {
            consumer.Close();
        }
    }
}
