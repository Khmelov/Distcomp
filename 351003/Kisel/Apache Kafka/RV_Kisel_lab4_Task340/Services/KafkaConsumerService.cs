using Confluent.Kafka;

namespace RV_Kisel_lab2_Task320.Services; // Проверьте этот namespace!

public class KafkaConsumerService : BackgroundService
{
    private readonly IConfiguration _config;
    private readonly ILogger<KafkaConsumerService> _logger;

    public KafkaConsumerService(IConfiguration config, ILogger<KafkaConsumerService> logger)
    {
        _config = config;
        _logger = logger;
    }

    protected override Task ExecuteAsync(CancellationToken stoppingToken)
    {
        // Обертываем в Task.Run, чтобы НЕ БЛОКИРОВАТЬ запуск веб-сервера!
        return Task.Run(() =>
        {
            var consumerConfig = new ConsumerConfig
            {
                BootstrapServers = _config["Kafka:BootstrapServers"] ?? "localhost:9092",
                GroupId = "publisher-group",
                AutoOffsetReset = AutoOffsetReset.Earliest
            };

            using var consumer = new ConsumerBuilder<string, string>(consumerConfig).Build();
            consumer.Subscribe("OutTopic");

            while (!stoppingToken.IsCancellationRequested)
            {
                try
                {
                    // Теперь эта блокирующая операция работает в фоне
                    var consumeResult = consumer.Consume(stoppingToken);
                    var message = consumeResult.Message.Value;
                    
                    _logger.LogInformation($"[KAFKA] Received from OutTopic: {message}");
                }
                catch (Exception ex)
                {
                    _logger.LogError($"Error consuming Kafka message: {ex.Message}");
                }
            }
        }, stoppingToken);
    }
}