using Confluent.Kafka;
using Discussion.Dtos.Kafka;
using Discussion.Services;
using Shared.Dtos;
using System.Text.Json;

public class InTopicConsumer : BackgroundService
{
    private IConsumer<string, string>? _consumer;
    private IProducer<string, string>? _producer;
    private readonly IReactionService _reactionService;
    private readonly ILogger<InTopicConsumer> _logger;
    private readonly ConsumerConfig _consumerConfig;
    private readonly ProducerConfig _producerConfig;

    public InTopicConsumer(IReactionService reactionService, ILogger<InTopicConsumer> logger)
    {
        _reactionService = reactionService;
        _logger = logger;

        _consumerConfig = new ConsumerConfig
        {
            BootstrapServers = "localhost:9092",
            GroupId = "discussion-group",
            AutoOffsetReset = AutoOffsetReset.Earliest,
            AllowAutoCreateTopics = true,
            SessionTimeoutMs = 6000,
            MaxPollIntervalMs = 300000
        };

        _producerConfig = new ProducerConfig
        {
            BootstrapServers = "localhost:9092",
            AllowAutoCreateTopics = true
        };
    }

    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        await Task.Yield();
        _consumer = new ConsumerBuilder<string, string>(_consumerConfig).Build();
        _producer = new ProducerBuilder<string, string>(_producerConfig).Build();
        
        _consumer.Subscribe("InTopic");
        _consumer.Consume(TimeSpan.FromMilliseconds(500));
        _logger.LogInformation("InTopicConsumer started.");

        while (!stoppingToken.IsCancellationRequested)
        {
            try
            {
                var cr = _consumer.Consume(TimeSpan.FromMilliseconds(100));
                if (cr?.Message?.Value == null) continue;

                // 1. Десериализуем основной "конверт"
                var msg = JsonSerializer.Deserialize<KafkaMessage>(cr.Message.Value,
                    new JsonSerializerOptions { PropertyNameCaseInsensitive = true });

                if (msg == null) continue;

                object? result = null;
                var jsonOptions = new JsonSerializerOptions { PropertyNameCaseInsensitive = true };

                // 2. ДИСПЕТЧЕР
                switch (msg.Method)
                {
                    case "CREATE":
                        var createReq = JsonSerializer.Deserialize<ReactionRequestTo>(msg.Payload.GetRawText(), jsonOptions);
                        if (createReq != null)
                        {
                            _logger.LogInformation("Creating reaction in DB with ID: {Id} from Publisher", createReq.Id);
                            createReq.State = Moderate(createReq.Content) ? "APPROVE" : "DECLINE";
                            await _reactionService.CreateAsync(createReq);
                        }
                        
                        continue;

                    case "FIND_BY_ID":
                        var findId = msg.Payload.Deserialize<long>(jsonOptions);
                        result = await _reactionService.GetByIdOnlyAsync(findId);
                        break;

                    case "GET_ALL":
                        result = await _reactionService.GetAllAsync();
                        break;

                    case "GET_BY_TOPIC_ID":
                        var tId = msg.Payload.Deserialize<long>(jsonOptions);
                        result = await _reactionService.GetByTopicIdAsync(tId);
                        break;

                    case "UPDATE":
                        var updateReq = JsonSerializer.Deserialize<ReactionRequestTo>(msg.Payload.GetRawText(), jsonOptions);
                        if (updateReq != null)
                        {
                            updateReq.State = Moderate(updateReq.Content) ? "APPROVE" : "DECLINE";
                            result = await _reactionService.UpdateAsync(updateReq);
                        }
                        break;

                    case "DELETE":
                        var deleteId = msg.Payload.Deserialize<long>(jsonOptions);
                        await _reactionService.DeleteAsync(deleteId);
                        result = new { Success = true, Id = deleteId };
                        break;

                    default:
                        _logger.LogWarning("Unknown method: {Method}", msg.Method);
                        break;
                }
                // 3. ОТПРАВКА ОТВЕТА
                if (!string.IsNullOrEmpty(msg.CorrelationId))
                {
                    
                    var responseEnvelope = new KafkaMessage
                    {
                        CorrelationId = msg.CorrelationId,
                        Method = msg.Method,
                        Payload = JsonSerializer.SerializeToElement(result)
                    };

                    var val = JsonSerializer.Serialize(responseEnvelope);

                    _logger.LogInformation("Sending response for {Method}, CorrelationId: {Id}", msg.Method, msg.CorrelationId);

                    await _producer.ProduceAsync("OutTopic", new Message<string, string>
                    {
                        Key = cr.Message.Key ?? "default", // Используем тот же ключ, что пришел
                        Value = val
                    });
                }
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error processing Kafka message");
                await Task.Delay(1000, stoppingToken);
            }
        }
    }

    private bool Moderate(string content) =>
        !string.IsNullOrEmpty(content) && !content.Contains("spam", StringComparison.OrdinalIgnoreCase);

    public override void Dispose()
    {
        _consumer?.Close();
        _consumer?.Dispose();
        _producer?.Dispose();
        base.Dispose();
    }
}