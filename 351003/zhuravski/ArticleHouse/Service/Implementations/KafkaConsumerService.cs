using Confluent.Kafka;

namespace ArticleHouse.Service.Implementations;

public class KafkaConsumerService : BackgroundService
{
    private readonly string topic;
    private readonly IConsumer<string, string> consumer;
    private readonly ILogger<KafkaConsumerService> logger;

    public KafkaConsumerService(IConfiguration configuration, ILogger<KafkaConsumerService> logger)
    {
        this.logger = logger;
        topic = configuration["Kafka:Topic"] ?? "default-topic";
        string bootstrapServers = configuration["Kafka:BootstrapServers"] ?? "localhost:9092";
        string groupId = configuration["Kafka:GroupId"] ?? "default-group";

        ConsumerConfig consumerConfig = new()
        {
            BootstrapServers = bootstrapServers,
            GroupId = groupId,
            AutoOffsetReset = AutoOffsetReset.Earliest,
            AllowAutoCreateTopics = true,
            EnableAutoCommit = false
        };
        consumer = new ConsumerBuilder<string, string>(consumerConfig).Build();
    }

    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        consumer.Subscribe(topic);
        try
        {
            while (!stoppingToken.IsCancellationRequested)
            {
                try
                {
                    var consumeResult = consumer.Consume(stoppingToken);
                    if (consumeResult != null)
                    {
                        await HandleMessageAsync(consumeResult.Message.Key, consumeResult.Message.Value);
                        consumer.Commit(consumeResult);
                    }
                }
                catch (ConsumeException e)
                {
                    logger.LogError(e, $"Consumption error: {e.Error.Reason}");
                    await Task.Delay(1000, stoppingToken);
                }
                catch (OperationCanceledException)
                {
                    break;
                }
            }
        }
        finally
        {
            consumer.Close();
        }
    }

    private async Task HandleMessageAsync(string key, string value)
    {
        logger.LogInformation($"Получено сообщение: Key={key}, Value={value}");
    }
}